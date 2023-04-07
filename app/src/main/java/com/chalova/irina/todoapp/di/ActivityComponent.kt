package com.chalova.irina.todoapp.di

import com.chalova.irina.todoapp.MainActivity
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
    fun addEditFragmentComponentFactory(): AddEditFragmentComponent.Factory
    fun userProfileFragmentComponentFactory(): UserProfileFragmentComponent.Factory
    fun bottomCalendarFragmentComponentFactory(): BottomCalendarFragmentComponent.Factory
}
