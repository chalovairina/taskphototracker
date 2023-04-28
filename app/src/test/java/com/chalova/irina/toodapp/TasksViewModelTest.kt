package com.chalova.irina.toodapp

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.chalova.irina.taskphototracker.config.AppConfig
import com.chalova.irina.taskphototracker.login_auth.domain.*
import com.chalova.irina.taskphototracker.tasks.data.util.Priority
import com.chalova.irina.taskphototracker.tasks.domain.*
import com.chalova.irina.taskphototracker.tasks.presentation.tasks.TasksEvent
import com.chalova.irina.taskphototracker.tasks.presentation.tasks.TasksViewModel
import com.chalova.irina.taskphototracker.tasks.utils.TaskOrder
import com.chalova.irina.toodapp.domain.*
import com.chalova.irina.toodapp.domain.login_auth.*
import com.chalova.irina.toodapp.domain.tasks.*
import com.chalova.irina.toodapp.util.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.*
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.Instant

const val testAuthority = "example.com"
const val testUri = "http://example.com"

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class TasksViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TasksViewModel
    private lateinit var tasksProvider: TasksProvider
    private lateinit var userUseCases: UserUseCases
    private lateinit var tasksUseCases: TasksUseCases
    private lateinit var savedStateHandle: SavedStateHandle

    private val testUserId = "userId"
    private val testTitle = "title"
    private val testDescription = "description"
    private val testDueDate = Instant.now()
    private val testPriority = Priority.LOW

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        val uriMock = mockk<Uri>()
        every { Uri.parse(testUri) } returns uriMock
        tasksProvider = TasksProvider()
        userUseCases = UserUseCases(
            FakeGetCurrentUserId(), FakeGetCurrentAuthData(), FakeGetLoginStatus(),
            FakeGetUserId(), FakeLogout(), FakeAuthenticateToken(), FakeUpdateToken(), FakeUpdateLoginStatus(),
            FakeGetAuthServiceData()
        )
        tasksUseCases = TasksUseCases(
            FakeAddTask(tasksProvider), FakeUpdateTask(tasksProvider), FakeDeleteTask(tasksProvider),
            FakeDeleteTasks(tasksProvider), FakeDeleteAllTasks(tasksProvider),
            FakeCompleteTask(tasksProvider), FakeGetTask(tasksProvider), FakeGetTasks(tasksProvider),
            FakeGetSearchQueryTasks(tasksProvider))
        savedStateHandle = SavedStateHandle()
        savedStateHandle[AppConfig.USER_ID] = testUserId
        viewModel = TasksViewModel(tasksUseCases, userUseCases, savedStateHandle)
    }

    @Test
    fun `onEvent DeleteTask task is not returned`() = runTest {

        viewModel.tasksState.test {
            awaitItem() // default empty emission
            // given
            val task = tasksUseCases.getTasks().firstOrNull()?.find { t -> t.title == "a" }

            // when
            viewModel.onEvent(TasksEvent.DeleteTask(task!!))

            // then
            val emission = awaitItem()
            val tasks = emission.tasksList!!
            assertTrue(tasks.find { it.title == "a" } == null)
        }
    }

    @Test
    fun `onEvent DeleteAll no tasks returned`() = runTest {

        viewModel.tasksState.test {
            // given
            awaitItem() // default empty emission

            // when
            viewModel.onEvent(TasksEvent.DeleteAll)

            // then
            val emission = awaitItem()
            val tasks = emission.tasksList
            assertTrue(tasks.isEmpty())
        }
    }

    @Test
    fun `onEvent RestoreTask task is not removed`() = runTest {
        viewModel.tasksState.test {
            awaitItem() // default empty emission
            // given
            val task = tasksUseCases.getTasks().firstOrNull()?.find { t -> t.title == "a" }
            // when
            viewModel.onEvent(TasksEvent.DeleteTask(task!!))
            var emission = awaitItem() // updated list with task deleted

            viewModel.onEvent(TasksEvent.RestoreTask)

            // then
            emission = awaitItem()
            val tasks = emission.tasksList
            assertTrue(tasks.find { it.title == "a" } != null)
        }
    }

    @Test
    fun `onEvent OnOrderChanged correct ordered tasks returned`() = runTest {
        // given
        val newOrder = TaskOrder.Date(TaskOrder.OrderType.Ascending)

        // when
        viewModel.onEvent(TasksEvent.OrderChanged(newOrder))
        viewModel.tasksState.test {
            var emission = awaitItem() // default value
            emission = awaitItem() // first loaded value
            emission = awaitItem() // order changed

            val tasks = emission.tasksList

            // then
            for (i in 0..tasks.size - 2) {
                assertTrue(tasks[i].dueDate < tasks[i + 1].dueDate)
            }
        }
    }

//    @Test
//    fun `onEvent OnSearchQueryChanged required tasks returned`() = runTest {
//        viewModel.tasksState.test {
//            // given
//            var emission = awaitItem() // default empty emission
//            // when
//            viewModel.onEvent(TasksEvent.SearchQueryChanged("b"))
//
//            // then
//            emission = awaitItem() // search query changed
//            val tasks = emission.tasksList
//            assertTrue(tasks.size == 1)
//        }
//    }
}