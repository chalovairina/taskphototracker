package com.chalova.irina.taskphototracker.di.fragment_addedit_scope

import com.chalova.irina.taskphototracker.tasks.presentation.addedittask.AddEditTaskFragment
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