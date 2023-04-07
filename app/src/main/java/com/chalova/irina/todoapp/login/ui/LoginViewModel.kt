package com.chalova.irina.todoapp.login.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.di.LoginScope
import com.chalova.irina.todoapp.login.data.repository.AuthConnectionDetails
import com.chalova.irina.todoapp.login.data.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@LoginScope
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val loginConnectionDetails = authRepository.authConnectionData
    private val _loginResult: MutableStateFlow<LoginResult> = MutableStateFlow(LoginResult.NotDefined)
    val loginState: StateFlow<LoginState> = combine(_isLoading, _loginResult) {
            isLoading, loginResult ->
        LoginState(
            isLoading = isLoading,
            loginUrl = getLoginUrl(loginConnectionDetails),
            authUrlAuthority = loginConnectionDetails.authUrlAuthority,
            redirectUrl = loginConnectionDetails.redirectUrl,
            loginResult = loginResult)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = LoginState(
            loginUrl = getLoginUrl(loginConnectionDetails),
            authUrlAuthority = loginConnectionDetails.authUrlAuthority,
            redirectUrl = loginConnectionDetails.redirectUrl
        )
    )

    private fun getLoginUrl(authDetails: AuthConnectionDetails): Uri {
        return authDetails.authUrl.buildUpon().apply {
            authDetails.authUrlParams.forEach {
                appendQueryParameter(it.key, it.value)
            }
        }.build()
    }

    fun onLoginEvent(loginEvent: LoginEvent) {
        when (loginEvent) {
            is LoginEvent.UrlLoadStart -> {
                _isLoading.update { true }
            }
            is LoginEvent.UrlLoaded -> {
                processAuthUrlLoaded(loginEvent.url)
                _isLoading.update { false }
            }
            is LoginEvent.LoadFailed -> {
                _isLoading.update { false }
            }
        }
    }

    private fun processAuthUrlLoaded(url: String) {
        checkForAuthRedirect(url)
    }

    private fun checkForAuthRedirect(url: String) {
        if (url.contains(loginState.value.redirectUrl.toString())) {
            val token = authRepository.extractToken(url)
            token?.let {
                val userId = authRepository.extractUserId(url)
                userId?.let {
                    viewModelScope.launch {
                        _loginResult.update { LoginResult.Success(userId, token) }
                    }
                } ?: run {
                    viewModelScope.launch {
                        _loginResult.update { LoginResult.Failed() }
                    }
                }
            } ?: run {
                viewModelScope.launch {
                    _loginResult.update { LoginResult.Failed() }
                }
            }
        }
    }
}