package com.chalova.irina.todoapp.di.activity_scope

import com.chalova.irina.todoapp.di.fragment_addedit_scope.AddEditFragmentComponent
import com.chalova.irina.todoapp.di.fragment_bottom_sheet_scope.BottomCalendarFragmentComponent
import com.chalova.irina.todoapp.di.fragment_tasks_scope.TasksFragmentComponent
import com.chalova.irina.todoapp.di.fragment_user_profile_scope.UserProfileFragmentComponent
import dagger.Module

@Module(
    subcomponents = [
        TasksFragmentComponent::class,
        AddEditFragmentComponent::class,
        UserProfileFragmentComponent::class,
        BottomCalendarFragmentComponent::class]
)
object ActivityModule