package com.chalova.irina.todoapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@LargeTest
class TasksScreenTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun when_add_button_clicked_add_edit_task_screen_is_opened() = runTest {
        onView(withId(R.id.vk_login_btn)).perform(click())

        onView(withId(R.id.current_title_editText)).check(matches(withText("")))
    }
}