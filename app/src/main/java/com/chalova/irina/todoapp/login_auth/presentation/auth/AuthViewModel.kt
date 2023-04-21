package com.chalova.irina.todoapp.login_auth.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.di.app_scope.AppScope
import com.chalova.irina.todoapp.login_auth.domain.UserUseCases
import com.chalova.irina.todoapp.login_auth.presentation.login.LoginStatus
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AppScope
class AuthViewModel @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _isTokenValid = MutableStateFlow(false)
    private val loginStatus = userUseCases.getLoginStatus()
    val authState: StateFlow<AuthState> =
        combine(loginStatus, _isTokenValid) { status, isTokenValid ->
            Timber.d("new state $status $isTokenValid")
            AuthState(loginStatus = status, isTokenValid = isTokenValid)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = AuthState()
            )
    private val _isLoading = MutableStateFlow(false)
    private val _userMessage: MutableSharedFlow<Int> = MutableSharedFlow()
    val userMessage: SharedFlow<Int> = _userMessage

    private var authenticateJob: Job? = null

    private fun authenticate() {
        if (authenticateJob != null) return

        authenticateJob = viewModelScope.launch {
            try {
                val authData = userUseCases.getCurrentAuthData()
                if (!authData.token.isNullOrEmpty() && !authData.userId.isNullOrEmpty()) {
                    authenticateToken(authData.userId, authData.token)
                } else {
                    when (userUseCases.logout()) {
                        is Result.Error -> _userMessage.emit(R.string.main_unknown_error)
                        is Result.Success -> _userMessage.emit(R.string.login_logout_successful)
                    }
                }
            } finally {
                authenticateJob = null
            }
        }
    }

    private suspend fun authenticateToken(userId: String, token: String) {
        when (val result = userUseCases.authenticateToken(userId, token)) {
            is Result.Error -> {
                _isTokenValid.update { false }

                when (result.errorResult) {
                    is ErrorResult.OAuthError -> {
                        logout()
                        _userMessage.emit(R.string.login_not_authorized)
                    }
                    is ErrorResult.NetworkError -> {
                        _userMessage.emit(R.string.login_network_error)
                    }
                    is ErrorResult.UnknownError -> {
                        _userMessage.emit(R.string.login_unknown_error)
                    }
                    else -> {
                        _userMessage.emit(R.string.login_unknown_error)
                    }
                }
            }
            is Result.Success -> {
                _isTokenValid.update { true }
                when (userUseCases.updateLoginStatus(LoginStatus.LoggedIn)) {
                    is Result.Success -> _userMessage.emit(R.string.login_successful)
                    is Result.Error -> _userMessage.emit(R.string.login_unknown_error)
                }
            }
        }
    }

    fun onAuthEvent(authEvent: AuthEvent) {
        when (authEvent) {
            is AuthEvent.LoginFailed -> {
                viewModelScope.launch {
                    userUseCases.updateLoginStatus(LoginStatus.LoggedOut)
                }
            }
            is AuthEvent.Logout -> {
                viewModelScope.launch { logout() }
            }
            is AuthEvent.LastLoginUnknown -> {
                _isLoading.update { true }
                authenticate()
                _isLoading.update { false }
            }
        }
    }

    private suspend fun logout() {
        _isTokenValid.update { false }
        userUseCases.logout()
    }
}