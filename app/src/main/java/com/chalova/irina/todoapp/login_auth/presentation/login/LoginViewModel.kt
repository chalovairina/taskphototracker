package com.chalova.irina.todoapp.login_auth.presentation.login

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.di.login_scope.LoginScope
import com.chalova.irina.todoapp.login_auth.data.AuthServiceData
import com.chalova.irina.todoapp.login_auth.domain.UserUseCases
import com.chalova.irina.todoapp.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@LoginScope
class LoginViewModel @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val authServiceData = userUseCases.getAuthServiceData()
    private val _loginResult: MutableStateFlow<LoginResult> =
        MutableStateFlow(LoginResult.NotDefined)
    val loginState: StateFlow<LoginState> =
        combine(_isLoading, _loginResult) { isLoading, loginResult ->
            LoginState(
                isLoading = isLoading,
                loginUrl = getLoginUrl(authServiceData),
                authUrlAuthority = authServiceData.authUrlAuthority,
                redirectUrl = authServiceData.redirectUrl,
                loginResult = loginResult
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = LoginState(
                loginUrl = getLoginUrl(authServiceData),
                authUrlAuthority = authServiceData.authUrlAuthority,
                redirectUrl = authServiceData.redirectUrl
            )
        )

    private fun getLoginUrl(authDetails: AuthServiceData): Uri {
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
        if (url.startsWith(loginState.value.redirectUrl.toString())) {
            val token = userUseCases.getAuthServiceData.extractToken(url)
            token?.let {
                val userId = userUseCases.getAuthServiceData.extractUserId(url)
                userId?.let {
                    viewModelScope.launch {
                        val tokenResult = userUseCases.updateToken(userId, token)
                        val statusResult = userUseCases.updateLoginStatus(LoginStatus.LoggedIn)
                        if (tokenResult is Result.Error || statusResult is Result.Error) {
                            _loginResult.update { LoginResult.Failed() }
                        } else {
                            _loginResult.update { LoginResult.Success(userId, token) }
                        }
                    }
                } ?: run {
                    _loginResult.update { LoginResult.Failed() }
                }
            } ?: run {
                _loginResult.update { LoginResult.Failed() }
            }
        }
    }
}