package com.chalova.irina.taskphototracker.photo_reports.domain

interface DeleteReports {

    suspend operator fun invoke(ids: List<Long>): com.chalova.irina.taskphototracker.utils.Result<Nothing>
}