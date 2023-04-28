package com.chalova.irina.taskphototracker.tasks.presentation.utils

import androidx.recyclerview.selection.SelectionTracker

interface SelectionTrackerProvider<T> {

    fun provideSelectionTracker(): SelectionTracker<T>
}