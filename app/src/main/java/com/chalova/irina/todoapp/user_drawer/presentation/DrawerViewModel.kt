package com.chalova.irina.todoapp.user_drawer.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.di.app_scope.AppScope
import com.chalova.irina.todoapp.user_profile.domain.UserProfileUseCases
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AppScope
class DrawerViewModel @Inject constructor(
    userProfileUseCases: UserProfileUseCases
) : ViewModel() {

    val drawerState: StateFlow<DrawerState> = userProfileUseCases.getUserProfile()
        .map { userProfile ->
            DrawerState(
                userEmail = userProfile?.email,
                imageUri = userProfile?.image?.let { Uri.parse(it) }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DrawerState()
        )
}