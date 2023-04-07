package com.chalova.irina.todoapp.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.di.AppScope
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import com.chalova.irina.todoapp.utils.ErrorResult
import com.chalova.irina.todoapp.utils.ServiceResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    val loginStatus = authRepository.loginStatus.distinctUntilChanged()
    private val userId = authRepository.userIdStream.distinctUntilChanged()
    private val _isLoading = MutableStateFlow(false)
    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _error: MutableStateFlow<ErrorResult?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState> = combine(_isLoading, _userMessage, _error, userId) {
            isLoading, userMessage, error, userId ->
        AuthState(
            isLoading = isLoading,
            userId = userId,
            authError = error,
            userMessage = userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AuthState(isLoading = true)
    )

    private var authenticateJob: Job? = null

    private fun authenticate() {
        if (authenticateJob != null) return

        authenticateJob = viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val userId = authRepository.getUserId()
                if (!token.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    authenticateToken(userId, token)
                } else {
                    authRepository.logout()
                }
            } finally {
                authenticateJob = null
            }
        }
    }

    private suspend fun authenticateToken(userId: String, token: String) {
        val result = authRepository.authenticate(userId, token)

        when (result) {
            is ServiceResult.Error -> {
                when (result.errorResult) {
                    is ErrorResult.OAuthError -> {
                        logout()
                        _userMessage.update { R.string.not_authorized }
                        _error.update {
                            ErrorResult.OAuthError()
                        }
                    }
                    is ErrorResult.NetworkError -> {
                        _userMessage.update { R.string.network_error }
                        _error.update {
                            ErrorResult.NetworkError()
                        }
                    }
                    is ErrorResult.UnknownError -> {
                        _userMessage.update { R.string.unknown_error }
                        _error.update {
                            ErrorResult.UnknownError()
                        }
                    }
                    else -> {
                        _userMessage.update { R.string.unknown_error }
                        _error.update {
                            ErrorResult.UnknownError()
                        }
                    }
                }
            }
            is ServiceResult.Success -> {
                _userMessage.update { R.string.login_successfull }
            }
        }
    }

    fun onAuthEvent(authEvent: AuthEvent) {
        when (authEvent) {
            is AuthEvent.Login -> {
                viewModelScope.launch {
                    authRepository.writeToken(authEvent.userId, authEvent.token)
                    authRepository.writeLoginStatus(LoginStatus.LoggedIn)
                }
            }
            is AuthEvent.LoginFailed -> {
                viewModelScope.launch {
                    authRepository.writeLoginStatus(LoginStatus.LoggedOut)
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

    fun onUserMessageShown() {
        _userMessage.update { null }
    }

    private suspend fun logout() {
        authRepository.logout()
    }
}