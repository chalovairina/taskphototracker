package com.chalova.irina.taskphototracker.photo_reports.domain

import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoReport
import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoResource
import com.chalova.irina.taskphototracker.tasks.domain.GetTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetPhotoReportsImpl(
    private val externalScope: CoroutineScope,
    private val getTasks: GetTasks
) : GetPhotoReports {

    override fun <T : PhotoResource> invoke(photoResources: List<T>): Flow<List<PhotoReport<T>>> {
        Timber.d("GetPhotoReportsImpl")
        return getTasks().map { tasks ->
            Timber.d("tasks $tasks")
            val reports = externalScope.async {
                val reports = mutableListOf<PhotoReport<T>>()
                val sortedPhotoResources = photoResources.sortedBy { it.name }
                Timber.d("photoResources $photoResources")
                tasks.sortedBy { it.reportPhoto }.forEach { task ->
                    if (task.reportPhoto != null) {
                        val photoRes =
                            sortedPhotoResources.find { it.name.startsWith(task.reportPhoto) }
                        Timber.d("Res $photoRes")
                        val report = PhotoReport(
                            taskId = task.id,
                            userId = task.userId,
                            taskTitle = task.title,
                            isCompleted = task.isCompleted,
                            photo = photoRes
                        )
                        reports.add(report)
                    }
                }
                reports.toList()
            }
            val result = reports.await()
            Timber.d("result $result")
            result
        }
    }
}