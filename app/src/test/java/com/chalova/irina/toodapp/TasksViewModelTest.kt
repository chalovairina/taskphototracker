package com.chalova.irina.toodapp

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.chalova.irina.todoapp.config.AppConfig
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.repository.TaskRepository
import com.chalova.irina.todoapp.tasks.data.util.DatabaseResult
import com.chalova.irina.todoapp.tasks.data.util.Priority
import com.chalova.irina.todoapp.tasks.ui.tasks.TasksEvent
import com.chalova.irina.todoapp.tasks.ui.tasks.TasksViewModel
import com.chalova.irina.todoapp.tasks.utils.TaskOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.*
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.Instant
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class TasksViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TasksViewModel

    private lateinit var fakeAuthRepository: AuthRepository
    private lateinit var fakeTaskRepository: TaskRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private val testUserId = "userId"
    private val testTitle = "title"
    private val testDescription = "description"
    private val testDueDate = Instant.now()
    private val testPriority = Priority.LOW
    private val testUri = "http://example.com"

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        val uriMock = mockk<Uri>()
        every { Uri.parse(testUri) } returns uriMock
        fakeAuthRepository = FakeAuthRepository()
        fakeTaskRepository = FakeTasksRepository()
        savedStateHandle = SavedStateHandle()
        savedStateHandle[AppConfig.USER_ID] = testUserId
        viewModel = TasksViewModel(fakeTaskRepository, savedStateHandle)

        val tasks = mutableListOf<Task>()
        ('a'..'z').forEachIndexed { i, c ->
            tasks.add(i, Task( id = i.toLong(),
                userId = testUserId, title = c.toString(),
                priority = Priority.values()[Random(0).nextInt(Priority.values().size)],
                dueDate = testDueDate.plusMillis(i.toLong())
            ))
        }
        tasks.shuffle()
        runTest { fakeTaskRepository.insertTasks(tasks) }
    }

    @Test
    fun `onEvent DeleteTask task is not returned`() = runTest {

        viewModel.tasksState.test {
            awaitItem() // default empty emission
            // given
            val task = fakeTaskRepository.getTasks(testUserId).map { it.find { t -> t.title == "a" }}.firstOrNull()
            var emission = awaitItem() // first initialized emission
            // when
            viewModel.onEvent(TasksEvent.DeleteTask(task!!))
            emission = awaitItem() // userMessage updated after delete
            assertTrue(emission.userMessage != null)

            // then
            emission = awaitItem()
            val tasks = emission.tasksResult.data!!
            assertTrue(tasks.find { it.title == "a"} == null)

        }
    }

    @Test
    fun `onEvent DeleteAll no tasks returned`() = runTest {

        viewModel.tasksState.test {
            awaitItem() // default empty emission
            // given
            var emission = awaitItem() // first initialized emission
            // when
            viewModel.onEvent(TasksEvent.DeleteAll)
            emission = awaitItem() // userMessage updated after delete
            assertTrue(emission.userMessage != null)

            // then
            emission = awaitItem()
            val tasks = emission.tasksResult
            assertTrue(tasks is DatabaseResult.Empty)
        }
    }

    @Test
    fun `onEvent RestoreTask task is not removed`() = runTest {
        viewModel.tasksState.test {
            awaitItem() // default empty emission
            // given
            val task = fakeTaskRepository.getTasks(testUserId).map { it.find { t -> t.title == "a" }}.firstOrNull()
            var emission = awaitItem() // first initialized emission
            // when
            viewModel.onEvent(TasksEvent.DeleteTask(task!!))
            emission = awaitItem() // userMessage updated after delete
            assertTrue(emission.userMessage != null)
            emission = awaitItem() // list updated with deleted task
            var tasks = emission.tasksResult.data!!
            assertTrue(tasks.find { it.title == "a"} == null)
            viewModel.onEvent(TasksEvent.RestoreTask)
            // then
            emission = awaitItem() // userMessage updated after restore
            assertTrue(emission.userMessage != null)

            emission = awaitItem() // insert restored task
            tasks = emission.tasksResult.data!!
            assertTrue(tasks.find { it.title == "a"} != null)
        }
    }

    @Test
    fun `onEvent OnOrderChanged correct ordered tasks returned`() = runTest {
        // given
        val newOrder = TaskOrder.Date(TaskOrder.OrderType.Ascending)

        // when
        viewModel.onEvent(TasksEvent.OnOrderChanged(newOrder))
        viewModel.tasksState.test {
            awaitItem()
            val emission = awaitItem()

            val tasks = emission.tasksResult.data!!

            // then
            for (i in 0..tasks.size - 2) {
                assertTrue(tasks[i].dueDate < tasks[i + 1].dueDate)
            }
        }
    }

    @Test
    fun `onEvent OnSearchQueryChanged required tasks returned`() = runTest {
        viewModel.tasksState.test {
            // given
            awaitItem() // default empty emission
            // when
            viewModel.onEvent(TasksEvent.OnSearchQueryChanged("z"))

            // then
            val emission = awaitItem()
            val tasks = emission.tasksResult.data!!
            assertTrue(tasks.size == 1)
        }
    }
}