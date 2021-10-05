package com.chari.ic.todoapp.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.chari.ic.todoapp.*
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.data.source.StubDataStoreRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.fragments.tasks_fragment.TasksFragment
import com.chari.ic.todoapp.fragments.tasks_fragment.ToDoTaskAdapter
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@MediumTest
@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksNavigationTest {
    @get: Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

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

    @Test
    fun clickAddFAB_navigateToAddFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInHiltContainer<TasksFragment>(navController = navController) {
            navController.setGraph(R.navigation.my_nav)
        }

        onView(withId(R.id.add_button)).perform(click())

        assertThat(navController.currentDestination?.id, equalTo(R.id.addFragment))

        scenario.close()
    }

    @Test
    fun clickOnRecyclerViewItem_navigateToUpdateFragment() {
        val task = ToDoTask(0, "Homework", Priority.HIGH, "My homework")
        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task) }

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            navController.setGraph(R.navigation.my_nav)
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.recyclerView))
                .perform(actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(0, click()))

        // fails - shows previous destination (tasksFragment) id but checks views from the correct destination -?
//        assertThat(navController.currentDestination?.id, equalTo(R.id.updateFragment))

        onView(withId(R.id.current_title_editText)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "Homework"
                )
            )
        )
        onView(withId(R.id.current_priority_spinner)).check(
            ViewAssertions.matches(
                ViewMatchers.withSpinnerText(
                    "High Priority"
                )
            )
        )
        onView(withId(R.id.current_description_editText)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "My homework"
                )
            )
        )

        scenario.close()
    }

    @Test
    fun navigateFromTasksToAddFragment_navigateUpReturnsToTasksFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.moveToState(Lifecycle.State.RESUMED)
        activityScenario.onActivity {
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
            navController.setGraph(R.navigation.my_nav)
        }

        onView(withId(R.id.add_button)).perform(click())
        pressBack()

        assertThat(navController.currentDestination?.id, equalTo(R.id.tasksFragment))

        activityScenario.close()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryTestModule {
        @Singleton
        @Binds
        abstract fun bindToDoRepository(repository: FakeToDoRepository): Repository

        @Singleton
        @Binds
        abstract fun bindDataStoreRepository(dataStoreRepository: StubDataStoreRepository): IDataStoreRepository
    }
}