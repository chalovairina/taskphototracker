package com.chalova.irina.todoapp.di

import com.chalova.irina.todoapp.user_profile.ui.UserProfileFragment
import dagger.Subcomponent

@Subcomponent
@UserProfileScope
interface UserProfileFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): UserProfileFragmentComponent
    }

    fun inject(fragment: UserProfileFragment)
}