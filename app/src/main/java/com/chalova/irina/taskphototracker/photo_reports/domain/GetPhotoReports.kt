package com.chalova.irina.taskphototracker.photo_reports.domain

import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoReport
import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoResource
import kotlinx.coroutines.flow.Flow

interface GetPhotoReports {

    operator fun <T : PhotoResource> invoke(photoResources: List<T>): Flow<List<PhotoReport<T>>>
}