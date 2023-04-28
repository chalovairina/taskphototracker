package com.chalova.irina.taskphototracker.di.app_scope

import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthManager
import com.chalova.irina.taskphototracker.login_auth.data.repository.AuthRepository
import com.chalova.irina.taskphototracker.login_auth.domain.*
import com.chalova.irina.taskphototracker.tasks.data.repository.TaskRepository
import com.chalova.irina.taskphototracker.tasks.domain.*
import com.chalova.irina.taskphototracker.user_profile.data.repository.UserRepository
import com.chalova.irina.taskphototracker.user_profile.domain.GetUserProfileImpl
import com.chalova.irina.taskphototracker.user_profile.domain.UpdateUserProfileImpl
import com.chalova.irina.taskphototracker.user_profile.domain.UserProfileUseCases
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object UseCaseModule {

    @Provides
    @AppScope
    fun userUseCases(
        authManager: AuthManager, authRepository: AuthRepository,
        externalScope: CoroutineScope
    ): UserUseCases = UserUseCases(
        GetCurrentUserIdImpl(authRepository),
        GetCurrentAuthDataImpl(externalScope, authRepository),
        GetLoginStatusImpl(externalScope, authRepository),
        GetUserIdImpl(externalScope, authRepository),
        LogoutImpl(authRepository),
        AuthenticateTokenImpl(authRepository),
        UpdateTokenImpl(authRepository),
        UpdateLoginStatusImpl(authRepository),
        GetAuthServiceDataImpl(authManager)
    )

    @Provides
    @AppScope
    fun tasksUseCases(
        userUseCases: UserUseCases, taskRepository: TaskRepository,
        externalScope: CoroutineScope
    ): TasksUseCases = TasksUseCases(
        AddTaskImpl(userUseCases, taskRepository),
        UpdateTaskImpl(taskRepository),
        DeleteTaskImpl(userUseCases.getCurrentUserId, taskRepository),
        DeleteTasksImpl(userUseCases.getCurrentUserId, taskRepository),
        DeleteAllTasksImpl(userUseCases.getCurrentUserId, taskRepository),
        CompleteTaskImpl(taskRepository),
        GetTaskImpl(userUseCases.getCurrentUserId, taskRepository),
        GetTasksImpl(externalScope, userUseCases.getUserId, taskRepository),
        GetSearchQueryTasksImpl(userUseCases.getCurrentUserId, taskRepository)
    )

    @Provides
    @AppScope
    fun userProfileUseCases(
        userUseCases: UserUseCases, userRepository: UserRepository,
        externalScope: CoroutineScope
    ): UserProfileUseCases = UserProfileUseCases(
        GetUserProfileImpl(externalScope, userUseCases.getUserId, userRepository),
        UpdateUserProfileImpl(userUseCases.getCurrentUserId, userRepository)
    )
}