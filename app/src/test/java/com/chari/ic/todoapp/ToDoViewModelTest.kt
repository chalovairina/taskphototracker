package com.chari.ic.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.data.source.LoggedInStubDataStoreRepository
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.Instant
import java.util.*

@ExperimentalCoroutinesApi
class ToDoViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var toDoViewModel: ToDoViewModel
    private lateinit var fakeRepository: Repository
    private lateinit var stubDataStoreRepository: IDataStoreRepository

    @Before
    fun setUp() {
        stubDataStoreRepository = LoggedInStubDataStoreRepository()
        fakeRepository = FakeToDoRepository()

        val task1 = ToDoTask(0, "1", "Homework1", Priority.LOW, "My homework1", Instant.now(),
            Instant.now(), false)
        val task2 = ToDoTask(0, "1", "Homework2", Priority.MEDIUM, "My homework2", Instant.now(),
            Instant.now(), false)
        val task3 = ToDoTask(0, "1", "Homework3", Priority.HIGH, "My homework3", Instant.now(),
            Instant.now(), false)
        runBlockingTest {
            (fakeRepository as FakeToDoRepository).fillTasksRepo(task1, task2, task3)
        }
        toDoViewModel = ToDoViewModel(fakeRepository, stubDataStoreRepository, Dispatchers.Main)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mainCoroutineRule.runBlockingTest {
            fakeRepository.resetRepository()
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertTask_ok() {
        val newTask = ToDoTask(
            0,
            "1",
            "Homework",
            Priority.LOW,
            "My homework",
            Instant.now(),
            Instant.now(),
            false
        )

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.insertTask(newTask)
        }

        val lastTask = toDoViewModel.getAllTasks.getOrAwaitValue().last()

        assertThat(lastTask, notNullValue())
        assertThat(lastTask.title, `is`(newTask.title))
        assertThat(lastTask.description, `is`(newTask.description))
        assertThat(lastTask.priority, `is`(newTask.priority))
    }

    @Test
    fun updateTask_ok() {
        val firstTask = toDoViewModel.getAllTasks.getOrAwaitValue().first()
        val newTitle = "Updated title"
        val newDescription = "Updated description"
        val newPriority = Priority.HIGH
        firstTask.title = newTitle
        firstTask.description = newDescription
        firstTask.priority = newPriority

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.updateTask(firstTask)
        }

        val updatedTaskFromDb = toDoViewModel.getAllTasks.getOrAwaitValue().first()
        assertThat(updatedTaskFromDb.title, `is`(newTitle))
        assertThat(updatedTaskFromDb.description, `is`(newDescription))
        assertThat(updatedTaskFromDb.priority, `is`(newPriority))
    }

    @Test
    @Throws(Exception::class)
    fun deleteTask_ok() {
        val taskToDelete = ToDoTask(
            0,
            "1",
            "Homework for delete",
            Priority.LOW,
            "My homework",
            Instant.now(),
            Instant.now(),
            false
        )

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.insertTask(taskToDelete)
        }


        val lastTask = toDoViewModel.getAllTasks.getOrAwaitValue().last()
        assertThat(lastTask, notNullValue())
        assertThat(lastTask.title, `is`("Homework for delete"))

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.deleteTask(lastTask)
        }
        val tasksFromDb = toDoViewModel.getAllTasks.getOrAwaitValue()

        val taskNotDeleted = tasksFromDb.contains(lastTask)
        assertThat(taskNotDeleted, `is`(false))
    }

    @Test
    fun deleteAll_ok() {
        val tasks = toDoViewModel.getAllTasks.getOrAwaitValue()
        assertThat(tasks.size, `is`(3))

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.deleteAll()
        }

        val deletedTasks = toDoViewModel.getAllTasks.getOrAwaitValue()
        assertThat(deletedTasks.size, `is`(0))
    }


}