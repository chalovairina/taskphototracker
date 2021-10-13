package com.chari.ic.todoapp.fragments.tasks_fragment

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chari.ic.todoapp.*
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.data.source.LoggedInStubDataStoreRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class TasksFragmentTest {
    @get: Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var fakeRepository: FakeToDoRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mainCoroutineRule.runBlockingTest {
            fakeRepository.resetRepository()
        }
    }

    // replace with fake tasks repo and loggedIn dataStore repo
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryTestModule {
        @Singleton
        @Binds
        abstract fun bindToDoRepository(repository: FakeToDoRepository): Repository

        @Singleton
        @Binds
        abstract fun bindDataStoreRepository(dataStoreRepository: LoggedInStubDataStoreRepository): IDataStoreRepository
    }

    // View Visibility Tests
    @Test
    fun dataAvailable_recyclerViewDisplayed_noDataViewsNotDisplayed() {
        Log.d("TasksFragmentTest", "repository = $fakeRepository")
        val task1 = ToDoTask(0, "1", "Homework1", Priority.LOW, "My homework1", Instant.now(),
            Instant.now(), false)

        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task1) }
        val scenario = launchFragmentInHiltContainer<TasksFragment>()

        onView(withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.no_data_textView))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        onView(withId(R.id.no_data_imageView))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))

        scenario.close()
    }

    @Test
    fun noDataAvailable_recyclerViewNotDisplayed_noDataViewsDisplayed() {
        val scenario = launchFragmentInHiltContainer<TasksFragment>()

        onView(withId(R.id.recyclerView))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        onView(withId(R.id.no_data_textView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.no_data_imageView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }

    // DRAG AND SWIPE TO DELETE TESTS
    @Test
    fun swipeToDelete_checkRecyclerViewChanged() {
        val task1 = ToDoTask(0, "1", "Homework1", Priority.LOW, "My homework1",
            Instant.now(), Instant.now(), false)
        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task1) }

        val scenario = launchFragmentInHiltContainer<TasksFragment>()

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(
                0, GeneralSwipeAction(
                    Swipe.SLOW, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT,
                    Press.FINGER
                )
            )
        )

        onView(withId(R.id.recyclerView))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        onView(withId(R.id.no_data_textView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.no_data_imageView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }

    @Test
    fun dragAndDropTaskInRecyclerView_checkTasksChangedPositions() {
        val task1 = ToDoTask(0, "1", "Homework1", Priority.LOW, "My homework1",
            Instant.now(), Instant.now(), false)
        val task2 = ToDoTask(0, "1", "Homework2", Priority.MEDIUM, "My homework2",
            Instant.now(), Instant.now(), false)
        val task3 = ToDoTask(0, "1", "Homework3", Priority.HIGH, "My homework3",
            Instant.now(), Instant.now(), false)
        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task1, task2, task3) }

        val scenario = launchFragmentInHiltContainer<TasksFragment>()

        onView(withId(R.id.recyclerView)).perform(
            DragAndDropViewAction(0, 2)
        )

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(0))
            .check(
                ViewAssertions.matches(
                    (ViewMatchers.hasDescendant(
                        ViewMatchers.withChild(
                            ViewMatchers.withText("Homework2")
                        )
                    ))
                )
            )
            .perform(RecyclerViewActions.scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(2))
            .check(
                ViewAssertions.matches(
                    (ViewMatchers.hasDescendant(
                        ViewMatchers.withChild(
                            ViewMatchers.withText("Homework1")
                        )
                    ))
                )
            )

        scenario.close()
    }
}