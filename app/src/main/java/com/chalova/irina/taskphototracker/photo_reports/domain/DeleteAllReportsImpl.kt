package com.chalova.irina.taskphototracker.photo_reports.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId
import com.chalova.irina.taskphototracker.photo_reports.data.repository.ReportRepository
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result

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