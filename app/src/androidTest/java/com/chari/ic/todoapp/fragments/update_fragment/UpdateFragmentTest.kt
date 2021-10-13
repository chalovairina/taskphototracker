package com.chari.ic.todoapp.fragments.update_fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.data.source.LoggedInStubDataStoreRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.launchFragmentInHiltContainer
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.util.*
import javax.inject.Singleton


@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@SmallTest
class UpdateFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    // replace with fake tasks repo and loggedIn dataStore repo
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryTestModule {
        @Singleton
        @Binds
        abstract fun bindToDoRepository(repository: FakeToDoRepository): Repository

        @Singleton
        @Binds
        abstract fun bindDataStoreRepository(dataStoreRepository: LoggedInStubDataStoreRepository): IDataStoreRepository
    }

    @Test
    fun taskPutIntoBundle_checkIfDisplayed() {
        val task = ToDoTask(
            0,
            "1",
            "Homework",
            Priority.LOW,
            "My homework",
            Instant.now(),
            Instant.now(),
            false
        )
        val bundle = UpdateFragmentArgs(task).toBundle()
        val scenario = launchFragmentInHiltContainer<UpdateFragment>(bundle)
        onView(withId(R.id.current_title_editText)).check(matches(withText("Homework")))
        onView(withId(R.id.current_priority_spinner)).check(matches(withSpinnerText("Low Priority")))
        onView(withId(R.id.current_description_editText)).check(matches(withText("My homework")))

        scenario.close()
    }

    @Test
    fun taskUpdate_DisplayedInUi() {
        val task = ToDoTask(
            0,
            "1",
            "Homework",
            Priority.LOW,
            "My homework",
            Instant.now(),
            Instant.now(),
            false
        )
        val bundle = UpdateFragmentArgs(task).toBundle()
        val scenario = launchFragmentInHiltContainer<UpdateFragment>(bundle)
        val newTitle = "Updated task title"
        onView(withId(R.id.current_title_editText)).perform(replaceText(newTitle)).check(matches(
            withText(newTitle)))

        scenario.close()
    }

    @Test
    fun taskUpdated_click() {
        val task = ToDoTask(
            0,
            "1",
            "Homework",
            Priority.LOW,
            "My homework",
            Instant.now(),
            Instant.now(),
            false
        )
        val bundle = UpdateFragmentArgs(task).toBundle()
        val scenario = launchFragmentInHiltContainer<UpdateFragment>(bundle)
        val newTitle = "Updated task title"
        onView(withId(R.id.current_title_editText)).perform(replaceText(newTitle)).check(matches(
            withText(newTitle)))

        scenario.close()
    }
}