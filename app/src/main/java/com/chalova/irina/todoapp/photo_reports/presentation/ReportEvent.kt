package com.chalova.irina.todoapp.photo_reports.presentation

import com.chalova.irina.todoapp.photo_reports.data.source.local.PhotoReport
import com.chalova.irina.todoapp.photo_reports.data.source.local.PhotoResource

sealed class ReportEvent {

    data class LoadPhotoReports<T : PhotoResource>(val photoResources: List<T>) : ReportEvent()
    data class DeleteReport<T : PhotoResource>(val photoReport: PhotoReport<T>) : ReportEvent()
    data class DeleteReports(val reportIds: List<Long>) : ReportEvent()
    object DeleteAll : ReportEvent()
}
