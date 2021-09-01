package com.chari.ic.todoapp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.fragments.tasks_fragment.ToDoTaskAdapter
import com.chari.ic.todoapp.repository.ToDoRepository
import com.chari.ic.todoapp.utils.PriorityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.IOException

@LargeTest
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class OverFlowMenuItemsTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainAndroidCoroutineRule()

    private lateinit var context: Context
    private lateinit var toDoViewModel: ToDoViewModel
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
        mainCoroutineRule.runBlockingTest { repository.fillTasksRepo(task1, task2, task3) }
        toDoViewModel = ToDoViewModel(repository, Dispatchers.Main)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mainCoroutineRule.runBlockingTest {
            repository.resetRepository()
        }
        database.clearAllTables()
        database.close()
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
            .check(matches((hasDescendant(withChild(withText(newTitle))))))
            .check(matches((hasDescendant(withChild(withText(newDescription))))))

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

        onView(withId(R.id.recyclerView)).check(matches(hasChildCount(4)))
            .perform(scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(4))
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
        onView(withId(R.id.recyclerView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.no_data_textView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.no_data_imageView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        activityScenario.close()
    }
}