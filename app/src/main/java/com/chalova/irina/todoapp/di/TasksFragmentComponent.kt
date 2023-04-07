package com.chalova.irina.todoapp.di

import com.chalova.irina.todoapp.tasks.ui.tasks.TasksFragment
import dagger.Subcomponent

@Subcomponent
@TasksFragmentScope
interface TasksFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): TasksFragmentComponent
    }
    fun inject(fragment: TasksFragment)
}