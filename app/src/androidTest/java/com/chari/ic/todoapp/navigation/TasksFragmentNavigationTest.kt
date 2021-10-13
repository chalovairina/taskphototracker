package com.chari.ic.todoapp.navigation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.chari.ic.todoapp.*
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.data.source.LoggedInStubDataStoreRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.fragments.tasks_fragment.TasksFragment
import com.chari.ic.todoapp.fragments.tasks_fragment.TasksFragmentDirections
import com.chari.ic.todoapp.fragments.tasks_fragment.ToDoTaskAdapter
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.inject.Inject
import javax.inject.Singleton

import com.chari.ic.todoapp.utils.matchesAndroidHome
import java.time.Instant
import java.util.*


@MediumTest
@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksFragmentNavigationTest {
    @get: Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var fakeRepository: FakeToDoRepository

    val task = ToDoTask(0, "1","Homework", Priority.HIGH, "My homework", Instant.now(),
        Instant.now(), false)

    @Before
    fun setUp() {
        hiltRule.inject()

        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task) }
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

    // Navigation to AddFragment
    @Test
    fun clickAddFAB_navigateToAddFragment() {
        val navController = mock(NavController::class.java)
        val scenario = launchFragmentInHiltContainer<TasksFragment>(navController = navController)

        onView(withId(R.id.add_button)).perform(click())

        verify(navController).navigate(R.id.action_tasksFragment_to_addFragment)

        scenario.close()
    }
    // up navigation
    @Test
    fun navigateToAddFragment_pressUpButtonReturnsToTasksFragment() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.add_button)).perform(click())

        onView(withId(R.id.title_editText)).check(matches(isDisplayed()))

        onView(matchesAndroidHome()).perform(click())

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    // back pressed
    @Test
    fun navigateToAddFragment_pressBackButtonReturnsToTasksFragment() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.add_button)).perform(click())

        onView(withId(R.id.title_editText)).check(matches(isDisplayed()))
        // to close keyboard
        pressBack()
        // to really go back
        pressBack()

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    // navigation to UpdateFragment
    @Test
    fun clickOnRecyclerViewItem_navigateToUpdateFragment() {

        val navController = mock(NavController::class.java)
        val scenario = launchFragmentInHiltContainer<TasksFragment>(navController = navController)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(0, click()))

        verify(navController).navigate(TasksFragmentDirections.actionTasksFragmentToUpdateFragment(task))

        scenario.close()
    }
    // up navigation
    @Test
    fun navigateToUpdateFragment_prssUpButtonReturnsToTasksFragment() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(0, click()))

        onView(withId(R.id.current_title_editText)).check(matches(isDisplayed()))

        onView(matchesAndroidHome()).perform(click())

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    // back pressed
    @Test
    fun navigateToUpdateFragment_pressBackButtonReturnsToTasksFragment() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(0, click()))

        onView(withId(R.id.current_title_editText)).check(matches(isDisplayed()))
        // to close keyboard
        pressBack()
        // to really go back
        pressBack()

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))

        activityScenario.close()
    }
}