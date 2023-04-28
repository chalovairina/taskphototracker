package com.chalova.irina.taskphototracker.photo_reports.domain

import com.chalova.irina.taskphototracker.utils.Result

interface DeleteAllReports {

    suspend operator fun invoke(): Result<Nothing>
}