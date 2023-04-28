package com.chalova.irina.todoapp.di.fragment_reports

import com.chalova.irina.todoapp.login_auth.domain.UserUseCases
import com.chalova.irina.todoapp.photo_reports.data.repository.ReportRepository
import com.chalova.irina.todoapp.photo_reports.domain.DeleteAllReportsImpl
import com.chalova.irina.todoapp.photo_reports.domain.DeleteReportsImpl
import com.chalova.irina.todoapp.photo_reports.domain.GetPhotoReportsImpl
import com.chalova.irina.todoapp.photo_reports.domain.ReportsUseCases
import com.chalova.irina.todoapp.tasks.domain.TasksUseCases
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object ReportsUseCaseModule {

    @ReportsFragmentScope
    @Provides
    fun provideReportsUseCases(
        externalScope: CoroutineScope,
        userUseCases: UserUseCases,
        tasksUseCases: TasksUseCases,
        reportRepository: ReportRepository
    ): ReportsUseCases {
        return ReportsUseCases(
            GetPhotoReportsImpl(externalScope, tasksUseCases.getTasks),
            DeleteReportsImpl(userUseCases.getCurrentUserId, reportRepository),
            DeleteAllReportsImpl(userUseCases.getCurrentUserId, reportRepository)
        )
    }
}