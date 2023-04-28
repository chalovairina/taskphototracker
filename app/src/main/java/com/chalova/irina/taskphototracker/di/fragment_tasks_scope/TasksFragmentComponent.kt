package com.chalova.irina.taskphototracker.di.fragment_tasks_scope

import com.chalova.irina.taskphototracker.tasks.presentation.tasks.TasksFragment
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