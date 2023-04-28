package com.chalova.irina.todoapp.photo_reports.domain

import com.chalova.irina.todoapp.login_auth.domain.GetCurrentUserId
import com.chalova.irina.todoapp.photo_reports.data.repository.ReportRepository
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.Result

class DeleteAllReportsImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val reportRepository: ReportRepository
) : DeleteAllReports {

    override suspend fun invoke(): Result<Nothing> {
        return getCurrentUserId()?.let { userId ->
            reportRepository.deleteAllReports(userId)
        } ?: Result.Error(ErrorResult.UserError())
    }
}