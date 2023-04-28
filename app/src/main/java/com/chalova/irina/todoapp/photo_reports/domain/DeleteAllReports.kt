package com.chalova.irina.todoapp.photo_reports.domain

import com.chalova.irina.todoapp.utils.Result

interface DeleteAllReports {

    suspend operator fun invoke(): Result<Nothing>
}