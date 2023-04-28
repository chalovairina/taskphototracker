package com.chalova.irina.taskphototracker.di.fragment_user_profile_scope

import com.chalova.irina.taskphototracker.user_profile.presentation.UserProfileFragment
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