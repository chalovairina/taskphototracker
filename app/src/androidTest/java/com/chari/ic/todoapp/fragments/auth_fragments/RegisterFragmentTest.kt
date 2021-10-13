package com.chari.ic.todoapp.fragments.auth_fragments

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chari.ic.todoapp.MainActivity
import com.chari.ic.todoapp.MainCoroutineRule
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.data.source.SignedOutStubDataStoreRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.repository.Repository
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
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
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RegisterFragmentTest {
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
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mainCoroutineRule.runBlockingTest {
            fakeRepository.resetRepository()
        }
    }

    // replace repository with fake tasks repo and signedOut dataStore repo
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
    fun enterAsSignedOut_checkIsDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.sign_up_btn_intro)).perform(click())

        onView(withId(R.id.sign_up_btn))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun emptyFields_checkErrorSnackBarShown() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.sign_up_btn_intro)).perform(click())

        onView(withId(R.id.sign_up_email_editText)).perform(replaceText(""))
        onView(withId(R.id.sign_up_password_editText)).perform(replaceText("password"))
        onView(withId(R.id.sign_up_username_editText)).perform(replaceText("username"))

        onView(withId(R.id.sign_up_btn)).perform(click())

        onView(ViewMatchers.withText(R.string.fill_in_email)).check(matches(isDisplayed()))

        onView(withId(R.id.sign_up_email_editText)).perform(replaceText("email"))
        onView(withId(R.id.sign_up_password_editText)).perform(replaceText(""))
        onView(withId(R.id.sign_up_username_editText)).perform(replaceText("username"))

        onView(withId(R.id.sign_up_btn)).perform(click())

        onView(ViewMatchers.withText(R.string.fill_in_password)).check(matches(isDisplayed()))

        onView(withId(R.id.sign_up_email_editText)).perform(replaceText("email"))
        onView(withId(R.id.sign_up_password_editText)).perform(replaceText("password"))
        onView(withId(R.id.sign_up_username_editText)).perform(replaceText(""))

        onView(withId(R.id.sign_up_btn)).perform(click())

        onView(withText(R.string.fill_in_username)).check(matches(isDisplayed()))

        activityScenario.close()
    }


    @Test
    fun emailTemplateIncorrect_checkErrorSnackBarShown() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.sign_up_btn_intro)).perform(click())

        onView(withId(R.id.sign_up_email_editText)).perform(replaceText("email"))
        onView(withId(R.id.sign_up_password_editText)).perform(replaceText("password"))
        onView(withId(R.id.sign_up_username_editText)).perform(replaceText("username"))

        onView(withId(R.id.sign_up_btn)).perform(click())

        onView(withText(R.string.email_from_incorrect)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun passwordNotStrongEnough_checkErrorSnackBarShown() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sign_up_btn_intro)).perform(click())

        onView(withId(R.id.sign_up_email_editText)).perform(replaceText("user@mail.ru"))
        onView(withId(R.id.sign_up_username_editText)).perform(replaceText("username"))
        // less then 8 letters
        onView(withId(R.id.sign_up_password_editText)).perform(replaceText("pasword"))

        onView(withId(R.id.sign_up_btn)).perform(click())
        onView(ViewMatchers.withText(R.string.password_not_strong_enough)).check(matches(isDisplayed()))

        // 8 letters + one capital letter - still not strong enough
        onView(withId(R.id.sign_up_password_editText)).perform(replaceText("pasSword"))
        onView(withId(R.id.sign_up_btn)).perform(click())
        onView(ViewMatchers.withText(R.string.password_not_strong_enough)).check(matches(isDisplayed()))

        // 8 letters + one capital letter + digit - still not strong enough
        onView(withId(R.id.sign_up_password_editText)).perform(replaceText("pasSword1"))
        onView(withId(R.id.sign_up_btn)).perform(click())
        onView(ViewMatchers.withText(R.string.password_not_strong_enough)).check(matches(isDisplayed()))

        activityScenario.close()
    }

}