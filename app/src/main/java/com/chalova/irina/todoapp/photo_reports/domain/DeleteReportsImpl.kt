package com.chalova.irina.todoapp.photo_reports.domain

import com.chalova.irina.todoapp.login_auth.domain.GetCurrentUserId
import com.chalova.irina.todoapp.photo_reports.data.repository.ReportRepository
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.Result

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