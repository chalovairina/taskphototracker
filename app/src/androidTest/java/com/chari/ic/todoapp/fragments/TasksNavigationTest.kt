package com.chari.ic.todoapp.fragments

import MainCoroutineRule
import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.chari.ic.todoapp.MainActivity
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.fragments.tasks_fragment.TasksFragment
import com.chari.ic.todoapp.fragments.tasks_fragment.ToDoTaskAdapter
import com.chari.ic.todoapp.repository.ToDoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import com.chari.ic.todoapp.fragments.tasks_fragment.TasksFragmentDirections
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksNavigationTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var context: Context
    private lateinit var repository: ToDoRepository
    private lateinit var database: ToDoDatabase
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        navController = mock(NavController::class.java)
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        ToDoRepository.initialize(database.getToDoDao())
        repository = ToDoRepository.getRepository()
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
    fun clickAddFAB_navigateToAddFragment() {
        val scenario = launchFragmentInContainer<TasksFragment>(
            Bundle(),
            R.style.Theme_TODOApp
        )

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.add_button)).perform(click())

        verify(navController).navigate(R.id.action_tasksFragment_to_addFragment)
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun clickOnRecyclerViewItem_navigateToUpdateFragment() {
        val task = ToDoTask(0, "Homework", Priority.HIGH, "My homework")
        mainCoroutineRule.runBlockingTest { repository.fillTasksRepo(task) }

        val scenario = launchFragmentInContainer<TasksFragment>(
            Bundle(),
            R.style.Theme_TODOApp
        )

        scenario.onFragment {
                fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(0, click()))

        verify(navController).navigate(TasksFragmentDirections.actionTasksFragmentToUpdateFragment(task))

        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun navigateFromTasksToAddFragment_navigateUpReturnsToTasksFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.moveToState(Lifecycle.State.RESUMED)
        activityScenario.onActivity {
                activity ->
            navController.setGraph(R.navigation.my_nav)
        }

        runOnUiThread {
            navController.navigate(R.id.action_tasksFragment_to_addFragment)
            navController.navigateUp()
        }

        assertThat(navController.currentDestination?.id, equalTo(R.id.tasksFragment))

        activityScenario.close()
    }

    @Test
    fun navigateFromTasksToAddFragment_pressBackButtonReturnsToTasksFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.moveToState(Lifecycle.State.RESUMED)
        activityScenario.onActivity {
                activity ->
            navController.setGraph(R.navigation.my_nav)
        }

        onView(withId(R.id.add_button)).perform(click())
        pressBack()

        assertThat(navController.currentDestination?.id, equalTo(R.id.tasksFragment))

        activityScenario.close()
    }
}