package com.chari.ic.todoapp.fragments.update_fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask

import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class UpdateFragmentTest {

    @Test
    fun taskPutIntoBundle_checkIfDisplayed() {
        val task = ToDoTask(
            0,
            "Homework",
            Priority.LOW,
            "My homework"
        )
        val bundle = UpdateFragmentArgs(task).toBundle()
        launchFragmentInContainer<UpdateFragment>(
            bundle,
            R.style.Theme_TODOApp
        )
        onView(withId(R.id.current_title_editText)).check(matches(withText("Homework")))
        onView(withId(R.id.current_priority_spinner)).check(matches(withSpinnerText("Low Priority")))
        onView(withId(R.id.current_description_editText)).check(matches(withText("My homework")))
    }

    @Test
    fun taskUpdate_DisplayedInUi() {
        val task = ToDoTask(
            0,
            "Homework",
            Priority.LOW,
            "My homework"
        )
        val bundle = UpdateFragmentArgs(task).toBundle()
        launchFragmentInContainer<UpdateFragment>(
            bundle,
            R.style.Theme_TODOApp
        )
        val newTitle = "Updated task title"
        onView(withId(R.id.current_title_editText)).perform(replaceText(newTitle)).check(matches(
            withText(newTitle)))
    }
}