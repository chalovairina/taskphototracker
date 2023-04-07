package com.chalova.irina.todoapp.di

import com.chalova.irina.todoapp.tasks.ui.addedittask.CalendarBottomSheetFragment
import dagger.Subcomponent

@Subcomponent
@BottomCalendarScope
interface BottomCalendarFragmentComponent {

    @Subcomponent.Factory
    interface Factory {

        fun create(): BottomCalendarFragmentComponent
    }

    fun inject(fragment: CalendarBottomSheetFragment)
}