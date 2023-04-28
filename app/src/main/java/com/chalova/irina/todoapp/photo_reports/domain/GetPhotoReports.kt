package com.chalova.irina.todoapp.photo_reports.domain

import com.chalova.irina.todoapp.photo_reports.data.source.local.PhotoReport
import com.chalova.irina.todoapp.photo_reports.data.source.local.PhotoResource
import kotlinx.coroutines.flow.Flow

interface GetPhotoReports {

    operator fun <T : PhotoResource> invoke(photoResources: List<T>): Flow<List<PhotoReport<T>>>
}