package com.chari.ic.todoapp.utils.idling_resource

interface RecyclerViewLayoutCompleteListener {
    // Callback to notify the idling resource that it can transition to the idle state
    fun onLayoutCompleted()

    fun setRecyclerViewLayoutComplete(completed: Boolean)
}