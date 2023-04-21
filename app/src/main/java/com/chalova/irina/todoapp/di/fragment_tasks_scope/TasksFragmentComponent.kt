package com.chalova.irina.todoapp.di.fragment_tasks_scope

import com.chalova.irina.todoapp.tasks.presentation.tasks.TasksFragment
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