package com.chari.ic.todoapp.fragments.tasks_fragment

import android.view.View
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf

class DragAndDropViewAction(private val sourceViewPosition: Int,
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