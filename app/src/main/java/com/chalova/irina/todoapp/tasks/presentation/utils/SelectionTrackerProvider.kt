package com.chalova.irina.todoapp.tasks.presentation.utils

import androidx.recyclerview.selection.SelectionTracker

interface SelectionTrackerProvider<T> {

    fun provideSelectionTracker(): SelectionTracker<T>
}