package com.chalova.irina.todoapp.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chalova.irina.todoapp.photo_reports.presentation.ReportsViewModel
import com.chalova.irina.todoapp.tasks.presentation.addedittask.AddEditViewModel
import com.chalova.irina.todoapp.tasks.presentation.addedittask.bottom_sheet_calendar.BottomCalendarViewModel
import com.chalova.irina.todoapp.tasks.presentation.tasks.TasksViewModel

// Tasks AssistedViewModelFactory
fun provideTasksFactory(
    assistedFactory: TasksViewModel.TasksViewModelFactory,
    savedStateHandle: SavedStateHandle
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(savedStateHandle) as T
        }
    }

// Reports AssistedViewModelFactory
fun provideReportsFactory(
    assistedFactory: ReportsViewModel.ReportsViewModelFactory,
    savedStateHandle: SavedStateHandle
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(savedStateHandle) as T
        }
    }

// AddEdit AssistedViewModelFactory
fun provideAddEditFactory(
    assistedFactory: AddEditViewModel.AddEditViewModelFactory,
    savedStateHandle: SavedStateHandle
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(savedStateHandle) as T
        }
    }

// BottomSheetCalendar AssistedViewModelFactory
fun provideBottomCalendarFactory(
    assistedFactory: BottomCalendarViewModel.BottomCalendarViewModelFactory,
    savedStateHandle: SavedStateHandle
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(savedStateHandle) as T
        }
    }