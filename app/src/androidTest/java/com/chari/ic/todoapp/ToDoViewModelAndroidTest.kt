package com.chari.ic.todoapp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ToDoViewModelAndroidTest {
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
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun test1_insertTask() {
        val newTask = ToDoTask(
            0,
            "Homework",
            Priority.LOW,
            "My homework"
        )

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.insertTask(newTask)
        }
        val taskFromDb = toDoViewModel.getAllTasks.getOrAwait().last()
        MatcherAssert.assertThat(taskFromDb, (Matchers.not(Matchers.nullValue())))
        MatcherAssert.assertThat(taskFromDb.title, Matchers.equalTo("Homework"))
    }

    @Test
    fun test2_updateTask_ok() {
        val firstTask = toDoViewModel.getAllTasks.getOrAwait().first()
        val newTitle = "Updated title"
        val newDescription = "Updated description"
        val newPriority = Priority.HIGH
        firstTask.title = newTitle
        firstTask.description = newDescription
        firstTask.priority = newPriority

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.updateTask(firstTask)
        }

        val updatedTaskFromDb = toDoViewModel.getAllTasks.getOrAwait().first()
        MatcherAssert.assertThat(updatedTaskFromDb.title, CoreMatchers.`is`(newTitle))
        MatcherAssert.assertThat(updatedTaskFromDb.description, CoreMatchers.`is`(newDescription))
        MatcherAssert.assertThat(updatedTaskFromDb.priority, CoreMatchers.`is`(newPriority))
    }

    @Test
    @Throws(Exception::class)
    fun test3_deleteTask_ok() {
        val taskToDelete = ToDoTask(
            0,
            "Homework for delete",
            Priority.LOW,
            "My homework"
        )

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.insertTask(taskToDelete)
        }


        val lastTask = toDoViewModel.getAllTasks.getOrAwait().last()
        MatcherAssert.assertThat(lastTask, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(lastTask.title, CoreMatchers.`is`("Homework for delete"))

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.deleteTask(lastTask)
        }
        val tasksFromDb = toDoViewModel.getAllTasks.getOrAwait()

        val taskNotDeleted = tasksFromDb.contains(lastTask)
        MatcherAssert.assertThat(taskNotDeleted, CoreMatchers.`is`(false))
    }

    @Test
    fun test4_deleteAll_ok() {
        val tasks = toDoViewModel.getAllTasks.getOrAwait()
        MatcherAssert.assertThat(tasks.size, CoreMatchers.`is`(3))

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.deleteAll()
        }

        val deletedTasks = toDoViewModel.getAllTasks.getOrAwait()
        MatcherAssert.assertThat(deletedTasks.size, CoreMatchers.`is`(0))
    }

}