package com.chari.ic.todoapp.utils.idling_resource.idling_resource_with_callback

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean


object RegisterIdlingResource: IdlingResource {
    private const val RESOURCE = "GLOBAL"

    @Volatile
    private var resourceCallback: ResourceCallback? = null

    private var isIdle = AtomicBoolean(true)

    override fun getName(): String = RegisterIdlingResource::class.java.name

    override fun isIdleNow(): Boolean {
        return isIdle.get()
    }

//    fun getCurrentActivity(): Activity? {
//        return InstrumentationRegistry.getInstrumentation().context.applicationContext as Activity
//    }

    /**
     * Sets the new idle state, if isIdleNow is true, it pings the [ResourceCallback].
     * @param isIdleNow false if there are pending operations, true if idle.
     */
    fun setIdleState(isIdleNow: Boolean) {
        if (isIdle.get() != isIdleNow) { isIdle.set(isIdleNow) }
        if (isIdleNow && resourceCallback != null) {
            resourceCallback!!.onTransitionToIdle()
        }
    }

    override fun registerIdleTransitionCallback(callback: ResourceCallback?) {
        if (callback != null) {
            resourceCallback = callback
        }
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
suspend fun IdlingResource.awaitUntilIdle() {
    // using loop because some times, registerIdleTransitionCallback wasn't called
    while (true) {
        Log.d("EspressoIdlingResource","Idling resource is idle = ${RegisterIdlingResource.isIdleNow}")
        if (isIdleNow) return
        delay(100)
    }
}
