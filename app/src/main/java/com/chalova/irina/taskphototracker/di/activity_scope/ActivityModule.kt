package com.chalova.irina.taskphototracker.di.activity_scope

import com.chalova.irina.taskphototracker.di.fragment_addedit_scope.AddEditFragmentComponent
import com.chalova.irina.taskphototracker.di.fragment_bottom_sheet_scope.BottomCalendarFragmentComponent
import com.chalova.irina.taskphototracker.di.fragment_reports.ReportsFragmentComponent
import com.chalova.irina.taskphototracker.di.fragment_tasks_scope.TasksFragmentComponent
import com.chalova.irina.taskphototracker.di.fragment_user_profile_scope.UserProfileFragmentComponent
import dagger.Module

@Module(
    subcomponents = [
        TasksFragmentComponent::class,
        ReportsFragmentComponent::class,
        AddEditFragmentComponent::class,
        UserProfileFragmentComponent::class,
        BottomCalendarFragmentComponent::class]
)
object ActivityModule