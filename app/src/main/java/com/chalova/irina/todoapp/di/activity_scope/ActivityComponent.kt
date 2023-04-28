package com.chalova.irina.todoapp.di.activity_scope

import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.di.fragment_addedit_scope.AddEditFragmentComponent
import com.chalova.irina.todoapp.di.fragment_bottom_sheet_scope.BottomCalendarFragmentComponent
import com.chalova.irina.todoapp.di.fragment_reports.ReportsFragmentComponent
import com.chalova.irina.todoapp.di.fragment_tasks_scope.TasksFragmentComponent
import com.chalova.irina.todoapp.di.fragment_user_profile_scope.UserProfileFragmentComponent
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
@ActivityScope
interface ActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }

    fun inject(activity: MainActivity)

    fun tasksFragmentComponentFactory(): TasksFragmentComponent.Factory
    fun reportsFragmentComponentFactory(): ReportsFragmentComponent.Factory
    fun addEditFragmentComponentFactory(): AddEditFragmentComponent.Factory
    fun userProfileFragmentComponentFactory(): UserProfileFragmentComponent.Factory
    fun bottomCalendarFragmentComponentFactory(): BottomCalendarFragmentComponent.Factory
}
