package com.chalova.irina.todoapp

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.di.AppScope
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import com.chalova.irina.todoapp.user_profile.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class DrawerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val userId = authRepository.userIdStream

    private val _drawerState = MutableStateFlow(DrawerState())
    val drawerState: StateFlow<DrawerState> = _drawerState

    private var userDataJob: Job? = null
    init {
        viewModelScope.launch {
            userId.collect { userId ->
                if (userId != null) {
                    userDataJob?.cancel()
                    userDataJob = userRepository.getUserData(userId).onEach { user ->
                        _drawerState.update {
                            it.copy(userId = user?.id,
                                userEmail = user?.email,
                                imageUri = user?.image?.let { Uri.parse(it) }
                            )
                        }
                    }.launchIn(viewModelScope)
                }
            }
        }
    }
}