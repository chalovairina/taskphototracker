package com.chalova.irina.todoapp.di.fragment_reports

import com.chalova.irina.todoapp.photo_reports.presentation.ReportsFragment
import dagger.Subcomponent

@Subcomponent(modules = [ReportsUseCaseModule::class])
@ReportsFragmentScope
interface ReportsFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ReportsFragmentComponent
    }

    fun inject(fragment: ReportsFragment)
}