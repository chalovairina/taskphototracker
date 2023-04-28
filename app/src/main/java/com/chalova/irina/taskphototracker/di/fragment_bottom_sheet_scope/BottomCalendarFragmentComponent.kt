package com.chalova.irina.taskphototracker.di.fragment_bottom_sheet_scope

import com.chalova.irina.taskphototracker.tasks.presentation.addedittask.bottom_sheet_calendar.CalendarBottomSheetFragment
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