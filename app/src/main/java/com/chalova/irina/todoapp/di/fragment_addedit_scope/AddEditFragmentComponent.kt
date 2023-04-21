package com.chalova.irina.todoapp.di.fragment_addedit_scope

import com.chalova.irina.todoapp.tasks.presentation.addedittask.AddEditTaskFragment
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