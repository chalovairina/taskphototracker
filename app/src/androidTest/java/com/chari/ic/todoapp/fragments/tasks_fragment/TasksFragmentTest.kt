package com.chari.ic.todoapp.fragments.tasks_fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewConfiguration
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chari.ic.todoapp.*
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.ToDoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainAndroidCoroutineRule()

    private lateinit var context: Context
    private lateinit var repository: ToDoRepository
    private lateinit var database: ToDoDatabase
    private lateinit var fragmentScenario: FragmentScenario<TasksFragment>


    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        ToDoRepository.initialize(database.getToDoDao())
        repository = ToDoRepository.getRepository()

        fragmentScenario = launchFragmentInContainer(Bundle(), R.style.Theme_TODOApp)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        fragmentScenario.moveToState(Lifecycle.State.DESTROYED)
        mainCoroutineRule.runBlockingTest {
            repository.resetRepository()
        }
        database.close()
    }

    @Test
    fun dataAvailable_recyclerViewDisplayed_noDataViewsNotDisplayed() {
        val task1 = ToDoTask(0, "Homework1", Priority.LOW, "My homework1")
        mainCoroutineRule.runBlockingTest { repository.fillTasksRepo(task1) }

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.no_data_textView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.no_data_imageView)).check(matches(not(isDisplayed())))

    }

    @Test
    fun noDataAvailable_recyclerViewNotDisplayed_noDataViewsDisplayed() {
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.no_data_textView)).check(matches(isDisplayed()))
        onView(withId(R.id.no_data_imageView)).check(matches(isDisplayed()))

    }

    @Test
    fun swipeToDelete_recyclerViewNotDisplayed_noDataViewsDisplayed() {
        val task1 = ToDoTask(0, "Homework1", Priority.LOW, "My homework1")
        mainCoroutineRule.runBlockingTest { repository.fillTasksRepo(task1) }

        onView(withId(R.id.recyclerView)).perform(
            actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(0, GeneralSwipeAction(
                    Swipe.SLOW, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT,
                Press.FINGER)
            )
        )

        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.no_data_textView)).check(matches(isDisplayed()))
        onView(withId(R.id.no_data_imageView)).check(matches(isDisplayed()))
    }

    @Test
    fun dragAndDropTaskInRecyclerView_checkTasksChangedPositions() {
        val task1 = ToDoTask(0, "Homework1", Priority.LOW, "My homework1")
        val task2 = ToDoTask(0, "Homework2", Priority.MEDIUM, "My homework2")
        val task3 = ToDoTask(0, "Homework3", Priority.HIGH, "My homework3")
        mainCoroutineRule.runBlockingTest { repository.fillTasksRepo(task1, task2, task3) }

        onView(withId(R.id.recyclerView)).perform(
            DragAndDropAction(0, 2)
        )

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(0))
            .check(matches((hasDescendant(withChild(withText("Homework2"))))))
            .perform(RecyclerViewActions.scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(2))
            .check(matches((hasDescendant(withChild(withText("Homework1"))))))
    }

    class DragAndDropAction(private val sourceViewPosition: Int,
                            private val targetViewPosition: Int) : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return allOf(isDisplayed(), isAssignableFrom(RecyclerView::class.java))
        }

        override fun getDescription(): String {
            return "Drag and drop action"
        }

        override fun perform(uiController: UiController, view: View) {
            val recyclerView: RecyclerView = view as RecyclerView
            //Sending down
            recyclerView.scrollToPosition(sourceViewPosition)
            uiController.loopMainThreadUntilIdle()
            val sourceView = recyclerView.findViewHolderForAdapterPosition(sourceViewPosition)?.itemView

            val sourceViewCenter = GeneralLocation.VISIBLE_CENTER.calculateCoordinates(sourceView)
            val fingerPrecision = Press.FINGER.describePrecision()

            val downEvent = MotionEvents.sendDown(uiController, sourceViewCenter, fingerPrecision).down
            try {
                // Factor 1.5 is needed, otherwise a long press is not safely detected.
                val longPressTimeout = (ViewConfiguration.getLongPressTimeout() * 1.5f).toLong()
                uiController.loopMainThreadForAtLeast(longPressTimeout)

                //Drag to the position
                recyclerView.scrollToPosition(targetViewPosition)
                uiController.loopMainThreadUntilIdle()
                val targetView = recyclerView.findViewHolderForAdapterPosition(targetViewPosition)?.itemView
                val targetViewLocation = if (targetViewPosition > sourceViewPosition) {
                    GeneralLocation.BOTTOM_CENTER.calculateCoordinates(targetView)
                } else {
                    GeneralLocation.TOP_CENTER.calculateCoordinates(targetView)
                }

                val steps = interpolate(sourceViewCenter, targetViewLocation)

                for (i in 0 until steps.size) {
                    if (!MotionEvents.sendMovement(uiController, downEvent, steps[i])) {
                        MotionEvents.sendCancel(uiController, downEvent)
                    }
                }

                //Release
                if (!MotionEvents.sendUp(uiController, downEvent, targetViewLocation)) {
                    MotionEvents.sendCancel(uiController, downEvent)
                }
            } finally {
                downEvent.recycle()
            }
        }

        // multiplier for full distance from start to end position to separate into steps
        private val SWIPE_EVENT_COUNT = 10

        private fun interpolate(start: FloatArray, end: FloatArray): Array<FloatArray> {
            val coord = Array(SWIPE_EVENT_COUNT) { FloatArray(2) }

            for (i in 1..SWIPE_EVENT_COUNT) {
                // get coordinates for each step (step = distance (end - start) / multiplier)
                    // for x value
                coord[i - 1][0] = start[0] + (end[0] - start[0]) * i / SWIPE_EVENT_COUNT
                // for y value
                coord[i - 1][1] = start[1] + (end[1] - start[1]) * i / SWIPE_EVENT_COUNT
            }

            return coord
        }
    }
}