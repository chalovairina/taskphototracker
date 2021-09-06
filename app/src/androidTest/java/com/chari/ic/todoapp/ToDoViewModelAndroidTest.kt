package com.chari.ic.todoapp

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.data.source.StubDataStoreRepository
import com.chari.ic.todoapp.repository.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import com.chari.ic.todoapp.repository.ToDoRepository
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ToDoViewModelAndroidTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    @Named("test_db")
    lateinit var database: ToDoDatabase
    private lateinit var stubDataStoreRepository: IDataStoreRepository
    private lateinit var repository: Repository
    private lateinit var toDoViewModel: ToDoViewModel

    @Before
    fun setUp() {
        hiltRule.inject()
        stubDataStoreRepository = StubDataStoreRepository()
        repository = ToDoRepository(database.getToDoDao())
        Log.d("ViewModelTest", "in Before fixture")

        val task1 = ToDoTask(0, "Homework1", Priority.LOW, "My homework1")
        val task2 = ToDoTask(0, "Homework2", Priority.MEDIUM, "My homework2")
        val task3 = ToDoTask(0, "Homework3", Priority.HIGH, "My homework3")
        mainCoroutineRule.runBlockingTest { repository.fillTasksRepo(task1, task2, task3) }

        toDoViewModel = ToDoViewModel(repository, stubDataStoreRepository, Dispatchers.Main)
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
        val tasksFromDb = toDoViewModel.getAllTasks.getOrAwaitValue()
        assertThat(tasksFromDb).contains(newTask)
    }

    @Test
    fun test2_updateTask_ok() {
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
        MatcherAssert.assertThat(updatedTaskFromDb.title, CoreMatchers.`is`(newTitle))
        MatcherAssert.assertThat(updatedTaskFromDb.description, CoreMatchers.`is`(newDescription))
        MatcherAssert.assertThat(updatedTaskFromDb.priority, CoreMatchers.`is`(newPriority))
    }

    @Test
    fun test3_deleteAll_ok() {
        val tasks = toDoViewModel.getAllTasks.getOrAwaitValue()
        MatcherAssert.assertThat(tasks.size, CoreMatchers.`is`(3))

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.deleteAll()
        }

        val deletedTasks = toDoViewModel.getAllTasks.getOrAwaitValue()
        MatcherAssert.assertThat(deletedTasks.size, CoreMatchers.`is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun test4_deleteTask_ok() {
        var taskToDelete = ToDoTask(
            0,
            "Homework for delete",
            Priority.LOW,
            "My homework"
        )

        mainCoroutineRule.runBlockingTest {
            toDoViewModel.deleteAll()
            toDoViewModel.insertTask(taskToDelete)
            taskToDelete = toDoViewModel.getAllTasks.getOrAwaitValue().first()
            toDoViewModel.deleteTask(taskToDelete)
        }

        val tasksFromDb = toDoViewModel.getAllTasks.getOrAwaitValue()
        assertThat(tasksFromDb).doesNotContain(taskToDelete)
    }

}