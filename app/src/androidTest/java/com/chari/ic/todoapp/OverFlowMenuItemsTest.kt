package com.chari.ic.todoapp

import MainCoroutineRule
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.fragments.tasks_fragment.ToDoTaskAdapter
import com.chari.ic.todoapp.repository.ToDoRepository
import com.chari.ic.todoapp.utils.PriorityUtils
import com.chari.ic.todoapp.utils.idling_resource.EspressoIdlingResource
import com.chari.ic.todoapp.utils.idling_resource.awaitUntilIdle
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.IOException


@LargeTest
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner::class)
class OverFlowMenuItemsTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var context: Context
    private lateinit var repository: ToDoRepository
    private lateinit var database: ToDoDatabase


    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        ToDoRepository.initialize(database.getToDoDao())
        repository = ToDoRepository.getRepository()
        val task1 = ToDoTask(0, "Homework1", Priority.LOW, "My homework1")
        val task2 = ToDoTask(0, "Homework2", Priority.MEDIUM, "My homework2")
        val task3 = ToDoTask(0, "Homework3", Priority.HIGH, "My homework3")
        val task4 = ToDoTask(0, "Special", Priority.MEDIUM, "My homework4")
        mainCoroutineRule.runBlockingTest { repository.fillTasksRepo(task1, task2, task3, task4) }
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mainCoroutineRule.runBlockingTest {
            repository.resetRepository()
        }
        database.close()
    }

    @Test
    fun test0_tasksFragment_clickOnSearchMenuItem_searchTaskByTitle_checkDisplayedOnlySelectedTasks() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        var searchQuery = "homework"
        onView(withId(R.id.menu_search))
            .perform(click())
        Log.d("Failing test", "menu item clicked, idlingRes is idle: ${EspressoIdlingResource.isIdleNow}")
        IdlingRegistry.getInstance().register(EspressoIdlingResource)

        onView(withId(R.id.search_src_text))
            .perform(clearText(), typeText(searchQuery))

        // fails on low resources either with commented code below or not - flaky test
//        runBlockingTest {
//                Log.d("FailingTest", "waiting to be idle.....")
//                EspressoIdlingResource.awaitUntilIdle()
//                Log.d("FailingTest", "idle status received")
//        }
//        Log.d("FailingTest", "after waiting to be idle")

        retryFlakyCode {
            onView(withId(R.id.recyclerView)).check(matches(hasChildCount(3)))
        }


        onView(withId(R.id.search_src_text))
            .perform(clearText())

        // fails on low resources either with commented code below or not - flaky test
//        runBlockingTest {
//            EspressoIdlingResource.awaitUntilIdle()
//        }

        retryFlakyCode {
            onView(withId(R.id.recyclerView)).check(matches(hasChildCount(4)))
        }

        searchQuery = "homework1"

        onView(withId(R.id.search_src_text))
            .perform(typeText(searchQuery))

        // fails on low resources either with commented code below or not - flaky test
//        runBlockingTest {
//            EspressoIdlingResource.awaitUntilIdle()
//        }

        retryFlakyCode {
            onView(withId(R.id.recyclerView)).check(matches(hasChildCount(1)))
        }

        IdlingRegistry.getInstance().unregister(EspressoIdlingResource)
        activityScenario.close()
    }

    @Test
    fun test1_updateFragment_clickOnSaveMenuItem_taskUpdatedAtTasksFragment_checkIfDisplayed() {
        val navController = TestNavHostController(context)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.moveToState(Lifecycle.State.RESUMED)
        activityScenario.onActivity {
                activity ->
            navController.setGraph(R.navigation.my_nav)
        }

        onView(withId(R.id.recyclerView))
            .perform(
                actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(
                    0,
                    click()
                )
            )

        val newTitle = "Updated task title"
        val newDescription = "Updated task description"

        onView(withId(R.id.current_title_editText)).perform(replaceText(newTitle))
        onView(withId(R.id.current_description_editText)).perform(replaceText(newDescription))
        onView(withId(R.id.current_priority_spinner)).perform(click())
        onData(`is`(instanceOf(String::class.java))).atPosition(PriorityUtils.PRIORITY_POSITION_HIGH)
            .perform(click())

        onView(withId(R.id.menu_save)).perform(click())

        assertThat(navController.currentDestination?.id, equalTo(R.id.tasksFragment))
        onView(withId(R.id.recyclerView))
            .perform(scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(0))
            .check(matches(hasDescendant(withChild(withText(newTitle)))))
            .check(matches(hasDescendant(withChild(withText(newDescription)))))

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
        // so that ui is not updated due to tearDown() method call
        activityScenario.close()
    }

    @Test
    fun test2_addFragment_clickOnAddMenuItem_taskAddedToTasksFragment_checkIfDisplayed() {
        val navController = TestNavHostController(context)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
                activity ->
            navController.setGraph(R.navigation.my_nav)
        }

        onView(withId(R.id.add_button)).perform(click())

        val newTitle = "New task title"
        val newDescription = "New task description"

        onView(withId(R.id.title_editText)).perform(typeText(newTitle))
        onView(withId(R.id.description_editText)).perform(typeText(newDescription))
        onView(withId(R.id.priority_spinner)).perform(click())
        onData(`is`(instanceOf(String::class.java))).atPosition(PriorityUtils.PRIORITY_POSITION_HIGH)
            .perform(click())

        onView(withId(R.id.menu_add)).perform(click())

        assertThat(navController.currentDestination?.id, equalTo(R.id.tasksFragment))

        onView(withId(R.id.recyclerView)).check(matches(hasChildCount(5)))
            .perform(scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(5))
            .check(matches((hasDescendant(withChild(withText(newTitle))))))
            .check(matches((hasDescendant(withChild(withText(newDescription))))))

        // so that ui is not updated due to tearDown() method call
        activityScenario.close()
    }

    @Test
    fun test3_tasksFragment_clickOnDeleteAllMenuItem_taskDeletedFromTasksFragment_checkIfDisplayed() {
        val navController = TestNavHostController(context)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
                activity ->
            navController.setGraph(R.navigation.my_nav)
        }

        openActionBarOverflowOrOptionsMenu(context)
        onView(withText("Delete All")).perform(click())

        onView(withText("YES"))
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id, equalTo(R.id.tasksFragment))
        onView(withId(R.id.recyclerView)).check(matches(CoreMatchers.not(isDisplayed())))
        onView(withId(R.id.no_data_textView)).check(matches(isDisplayed()))
        onView(withId(R.id.no_data_imageView)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun test4_tasksFragment_sortByHighPriority_checkSortOrder() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        openActionBarOverflowOrOptionsMenu(context)

        onView(withText("Sort By"))
            .perform(click())
        onView(withText("High Priority"))
            .perform(click())

        onView(withId(R.id.recyclerView))
            .check(matches(atPosition(0, hasDescendant(withText("Homework3")))))
            .check(matches(atPosition(3, hasDescendant(withText("Homework1")))))

        activityScenario.close()
    }

    @Test
    fun test5_tasksFragment_sortByLowPriority_checkSortOrder() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        openActionBarOverflowOrOptionsMenu(context)

        onView(withText("Sort By"))
            .perform(click())
        onView(withText("Low Priority"))
            .perform(click())

        onView(withId(R.id.recyclerView))
            .check(matches(atPosition(3, hasDescendant(withText("Homework3")))))
            .check(matches(atPosition(0, hasDescendant(withText("Homework1")))))

        activityScenario.close()
    }

    @Test
    fun test6_tasksFragment_sortByHighPriorityAndReset_checkOriginalSortOrder() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        openActionBarOverflowOrOptionsMenu(context)
        onView(withText("Sort By"))
            .perform(click())
        onView(withText("Low Priority"))
            .perform(click())

        openActionBarOverflowOrOptionsMenu(context)
        onView(withText("Sort By"))
            .perform(click())
        onView(withText("Reset"))
            .perform(click())

        onView(withId(R.id.recyclerView))
            .check(matches(atPosition(0, hasDescendant(withText("Special")))))
            .check(matches(atPosition(1, hasDescendant(withText("Homework3")))))
            .check(matches(atPosition(2, hasDescendant(withText("Homework2")))))
            .check(matches(atPosition(3, hasDescendant(withText("Homework1")))))

        activityScenario.close()
    }

    private fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    val timeoutMs = 500
    val intervalMs = 100

    private fun retryFlakyCode(action: () -> ViewInteraction): ViewInteraction {
        var cachedException: Throwable
        val startTime = System.currentTimeMillis()

        do {
            try {
                return action.invoke()
            } catch (e: Throwable) {
                Thread.sleep(intervalMs.toLong())
                cachedException = e
            }
        } while(System.currentTimeMillis() - startTime <= timeoutMs)

        throw cachedException
    }
}