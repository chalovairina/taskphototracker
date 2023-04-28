package com.chalova.irina.taskphototracker.photo_reports.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetCurrentUserId
import com.chalova.irina.taskphototracker.photo_reports.data.repository.ReportRepository
import com.chalova.irina.taskphototracker.utils.ErrorResult
import com.chalova.irina.taskphototracker.utils.Result

class DeleteReportsImpl(
    private val getCurrentUserId: GetCurrentUserId,
    private val reportRepository: ReportRepository
) : DeleteReports {

    override suspend operator fun invoke(ids: List<Long>): Result<Nothing> {
        return getCurrentUserId()?.let { userId ->
            reportRepository.deleteReports(userId, ids)
        } ?: Result.Error(ErrorResult.UserError())
    }
}