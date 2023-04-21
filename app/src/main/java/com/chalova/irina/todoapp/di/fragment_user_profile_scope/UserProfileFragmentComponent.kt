package com.chalova.irina.todoapp.di.fragment_user_profile_scope

import com.chalova.irina.todoapp.user_profile.presentation.UserProfileFragment
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