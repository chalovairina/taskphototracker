package com.chari.ic.todoapp.utils.idling_resource

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean


object EspressoIdlingResource: IdlingResource {
    private const val RESOURCE = "GLOBAL"

    @Volatile
    private var resourceCallback: ResourceCallback? = null

    private var mIsIdleNow = AtomicBoolean(false)

//    @JvmField
//    val countingIdleResource = CountingIdlingResource(RESOURCE)
//
//    fun increment() {
//        countingIdleResource.increment()
//    }
//
//    fun decrement() {
//        if (!countingIdleResource.isIdleNow) {
//            countingIdleResource.decrement()
//        }
//    }


    override fun getName() = "EspressoIdlingResource"

    override fun isIdleNow(): Boolean = mIsIdleNow.get()

    /**
     * Sets the new idle state, if isIdleNow is true, it pings the [ResourceCallback].
     * @param isIdleNow false if there are pending operations, true if idle.
     */
    fun setIdleState(isIdleNow: Boolean) {
        if (mIsIdleNow.get() != isIdleNow) { mIsIdleNow.set(isIdleNow) }
        if (isIdleNow && resourceCallback != null) {
            resourceCallback!!.onTransitionToIdle()
        }
    }

    override fun registerIdleTransitionCallback(callback: ResourceCallback?) {
        if (callback != null) {
            this.resourceCallback = callback
        }
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
suspend fun IdlingResource.awaitUntilIdle() {
    // using loop because some times, registerIdleTransitionCallback wasn't called
    while (true) {
        Log.d("EspressoIdlingResource","Idling resource is idle = ${EspressoIdlingResource.isIdleNow}")
        if (isIdleNow) return
        delay(100)
    }
}
