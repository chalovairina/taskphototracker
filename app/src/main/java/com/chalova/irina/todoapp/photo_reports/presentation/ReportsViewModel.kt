package com.chalova.irina.todoapp.photo_reports.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.chalova.irina.todoapp.photo_reports.data.source.local.PhotoReport
import com.chalova.irina.todoapp.photo_reports.data.source.local.PhotoResource
import com.chalova.irina.todoapp.photo_reports.domain.ReportsUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class ReportsViewModel @AssistedInject constructor(
    private val reportsUseCases: ReportsUseCases,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface ReportsViewModelFactory {

        fun create(savedStateHandle: SavedStateHandle): ReportsViewModel
    }

    fun <T : PhotoResource> getPhotoReports(photoResources: List<T>)
            : Flow<List<PhotoReport<T>>> {
        Timber.d("getPhotoReports for $photoResources")
        return reportsUseCases.getPhotoReports(photoResources)
    }
}