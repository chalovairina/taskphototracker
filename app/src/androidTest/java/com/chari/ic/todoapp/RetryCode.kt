package com.chari.ic.todoapp

import androidx.test.espresso.ViewInteraction

private val timeoutMs = 3000
private val intervalMs = 100

fun retryFlakyCode(action: () -> ViewInteraction): ViewInteraction {
    var cachedException: Throwable
    val startTime = System.currentTimeMillis()

    do {
        try {
            return action.invoke()
        } catch (e: Throwable) {
            Thread.sleep(intervalMs.toLong())
            cachedException = e
        }
    } while(System.currentTimeMillis() - startTime <= timeoutMs)

    throw cachedException
}

fun retryFlakyCodeWithoutReturnValue(action: () -> Unit) {
    var cachedException: Throwable? = null
    val startTime = System.currentTimeMillis()

    do {
        try {
            action.invoke()
        } catch (e: Throwable) {
            Thread.sleep(intervalMs.toLong())
            cachedException = e
        }
    } while(System.currentTimeMillis() - startTime <= timeoutMs)

    if (cachedException != null) {
        throw cachedException
    }
}