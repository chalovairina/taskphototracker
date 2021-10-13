package com.chari.ic.todoapp.navigation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.*
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chari.ic.todoapp.MainActivity
import com.chari.ic.todoapp.MainCoroutineRule
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.data.source.SignedOutStubDataStoreRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.fragments.tasks_fragment.TasksFragment
import com.chari.ic.todoapp.launchFragmentInHiltContainer
import com.chari.ic.todoapp.repository.Repository
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.utils.matchesAndroidHome
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.io.IOException
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class IntroFragmentNavigationTest {
    @get: Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var fakeRepository: FakeToDoRepository
    @ApplicationContext
    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()

        val task = ToDoTask(0, "1","Homework", Priority.HIGH, "My homework", Instant.now(),
            Instant.now(), false)
        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task) }
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mainCoroutineRule.runBlockingTest {
            fakeRepository.resetRepository()
        }
    }

    // replace repository with fake tasks repo and signedOutDataStore repo
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryTestModule {
        @Singleton
        @Binds
        abstract fun bindToDoRepository(repository: FakeToDoRepository): Repository

        @Singleton
        @Binds
        abstract fun bindDataStoreRepository(dataStoreRepository: SignedOutStubDataStoreRepository): IDataStoreRepository
    }

    @Test
    fun enterAsSignedOut_checkIntroFragmentIsDisplayed() {
        val navController = mock(NavController::class.java)
        val scenario = launchFragmentInHiltContainer<TasksFragment>(navController = navController)

        verify(navController).navigate(
            R.id.introFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.tasksFragment, true)
                .build())

        scenario.close()
    }

    // Navigation to LoginFragment
    @Test
    fun clickSingInButton_navigateToLoginFragment_checkIsDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sign_in_btn_intro)).perform(click())
        onView(withId(R.id.sign_in_btn)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    // up navigation
    @Test
    fun navigateToLoginFragment_pressUpButtonReturnsToIntroFragment() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sign_in_btn_intro)).perform(click())
        onView(withId(R.id.sign_in_btn)).check(matches(isDisplayed()))

        onView(matchesAndroidHome()).perform(click())
        onView(withId(R.id.let_s_get_started_tv)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    // back pressed
    @Test
    fun navigateToLoginFragment_pressBackButtonReturnsToIntroFragment() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sign_in_btn_intro)).perform(click())
        onView(withId(R.id.sign_in_btn)).check(matches(isDisplayed()))

        pressBack()
        onView(withId(R.id.let_s_get_started_tv)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    // Navigation to RegisterFragment
    @Test
    fun clickSingUpButton_navigateToRegisterFragment_checkIsDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sign_up_btn_intro)).perform(click())
        onView(withId(R.id.sign_up_btn)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    // up navigation
    @Test
    fun navigateToRegisterFragment_pressUpButtonReturnsToIntroFragment() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sign_up_btn_intro)).perform(click())
        onView(withId(R.id.sign_up_btn)).check(matches(isDisplayed()))

        onView(matchesAndroidHome()).perform(click())
        onView(withId(R.id.let_s_get_started_tv)).check(matches(isDisplayed()))

        activityScenario.close()
    }
    // back pressed
    @Test
    fun navigateToRegisterFragment_pressBackButtonReturnsToIntroFragment() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sign_up_btn_intro)).perform(click())
        onView(withId(R.id.sign_up_btn)).check(matches(isDisplayed()))

        pressBack()
        onView(withId(R.id.let_s_get_started_tv)).check(matches(isDisplayed()))

        activityScenario.close()
    }

}