package com.chalova.irina.taskphototracker.photo_reports.data.repository

import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoReport
import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoResource
import com.chalova.irina.taskphototracker.utils.Result
import kotlinx.coroutines.flow.Flow

interface ReportRepository {

    fun <T : PhotoResource> getReportsStream(userId: String): Flow<List<PhotoReport<T>>>

    suspend fun <T : PhotoResource> getReport(userId: String, taskId: Long): PhotoReport<T>?

    suspend fun <T : PhotoResource> insertReport(photoReport: PhotoReport<T>): Result<Nothing>

    suspend fun deleteReport(userId: String, reportId: Long): Result<Nothing>

    suspend fun deleteReports(userId: String, reportIds: List<Long>): Result<Nothing>

    suspend fun deleteAllReports(userId: String): Result<Nothing>
}