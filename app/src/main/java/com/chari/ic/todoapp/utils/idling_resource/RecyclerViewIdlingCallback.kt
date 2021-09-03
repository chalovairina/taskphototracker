package com.chari.ic.todoapp.utils.idling_resource

interface RecyclerViewIdlingCallback {
    fun setRecyclerViewLayoutCompleteListener(listener: RecyclerViewLayoutCompleteListener?)

    fun removeRecyclerViewLayoutCompleteListener(listener: RecyclerViewLayoutCompleteListener?)

    // Callback for the idling resource to check if the fragment containing the recyclerview
    // is idle
    fun isRecyclerViewLayoutCompleted(): Boolean
}