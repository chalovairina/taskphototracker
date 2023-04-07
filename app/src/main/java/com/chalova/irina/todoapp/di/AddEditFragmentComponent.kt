package com.chalova.irina.todoapp.di

import com.chalova.irina.todoapp.tasks.ui.addedittask.AddEditTaskFragment
import dagger.Subcomponent

@Subcomponent
@AddEditFragmentScope
interface AddEditFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AddEditFragmentComponent
    }

    fun inject(fragment: AddEditTaskFragment)
}