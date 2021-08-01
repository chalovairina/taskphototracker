package com.chari.ic.todoapp.fragments.tasks_fragment

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chari.ic.todoapp.*
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.fragments.update_fragment.UpdateFragment
import com.chari.ic.todoapp.fragments.update_fragment.UpdateFragmentArgs
import com.chari.ic.todoapp.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainAndroidCoroutineRule()

    protected lateinit var context: Context
    protected lateinit var toDoViewModel: ToDoViewModel
    protected lateinit var repository: ToDoRepository
    protected lateinit var database: ToDoDatabase


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

        var activityScenarioRule = ActivityScenario.launch(MainActivity::class.java)
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
    fun testRecyclerViewShown() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))

    }

    @Test
    fun testAddedTaskShown() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(0, click()))
        onView(withId(R.id.current_title_editText)).check(matches(withText("Homework1")))
        onView(withId(R.id.current_priority_spinner)).check(matches(withSpinnerText("Low Priority")))
        onView(withId(R.id.current_description_editText)).check(matches(withText("My homework1")))
    }
}