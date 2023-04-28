package com.chalova.irina.todoapp.photo_reports.domain

interface DeleteReports {

    suspend operator fun invoke(ids: List<Long>): com.chalova.irina.todoapp.utils.Result<Nothing>
}