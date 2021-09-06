package com.chari.ic.todoapp.fragments.tasks_fragment

import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chari.ic.todoapp.*
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.data.source.StubDataStoreRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.repository.IDataStoreRepository
import com.chari.ic.todoapp.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
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
class TasksFragmentTest {
    @get: Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

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

    // View Visibility Tests

    @Test
    fun dataAvailable_recyclerViewDisplayed_noDataViewsNotDisplayed() {
        Log.d("TasksFragmentTest", "repository = $fakeRepository")
        val task1 = ToDoTask(0, "Homework1", Priority.LOW, "My homework1")

        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task1) }
        val scenario = launchFragmentInHiltContainer<TasksFragment>()

        onView(withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.no_data_textView))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        onView(withId(R.id.no_data_imageView))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))

        scenario.close()
    }

    @Test
    fun noDataAvailable_recyclerViewNotDisplayed_noDataViewsDisplayed() {
        val scenario = launchFragmentInHiltContainer<TasksFragment>()

        onView(withId(R.id.recyclerView))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        onView(withId(R.id.no_data_textView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.no_data_imageView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }

    // DRAG AND SWIPE TO DELETE TESTS
    @Test
    fun swipeToDelete_recyclerViewNotDisplayed_noDataViewsDisplayed() {
        val task1 = ToDoTask(0, "Homework1", Priority.LOW, "My homework1")
        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task1) }

        val scenario = launchFragmentInHiltContainer<TasksFragment>()

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ToDoTaskAdapter.ToDoViewHolder>(
                0, GeneralSwipeAction(
                    Swipe.SLOW, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT,
                    Press.FINGER
                )
            )
        )

        onView(withId(R.id.recyclerView))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        onView(withId(R.id.no_data_textView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.no_data_imageView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        scenario.close()
    }

    @Test
    fun dragAndDropTaskInRecyclerView_checkTasksChangedPositions() {
        val task1 = ToDoTask(0, "Homework1", Priority.LOW, "My homework1")
        val task2 = ToDoTask(0, "Homework2", Priority.MEDIUM, "My homework2")
        val task3 = ToDoTask(0, "Homework3", Priority.HIGH, "My homework3")
        mainCoroutineRule.runBlockingTest { fakeRepository.fillTasksRepo(task1, task2, task3) }

        val scenario = launchFragmentInHiltContainer<TasksFragment>()

        onView(withId(R.id.recyclerView)).perform(
            DragAndDropAction(0, 2)
        )

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(0))
            .check(
                ViewAssertions.matches(
                    (ViewMatchers.hasDescendant(
                        ViewMatchers.withChild(
                            ViewMatchers.withText("Homework2")
                        )
                    ))
                )
            )
            .perform(RecyclerViewActions.scrollToPosition<ToDoTaskAdapter.ToDoViewHolder>(2))
            .check(
                ViewAssertions.matches(
                    (ViewMatchers.hasDescendant(
                        ViewMatchers.withChild(
                            ViewMatchers.withText("Homework1")
                        )
                    ))
                )
            )

        scenario.close()
    }

    class DragAndDropAction(private val sourceViewPosition: Int,
                            private val targetViewPosition: Int) : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return AllOf.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.isAssignableFrom(RecyclerView::class.java)
            )
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
    //@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
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