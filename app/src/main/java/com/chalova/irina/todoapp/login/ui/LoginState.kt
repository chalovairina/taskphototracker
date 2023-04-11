package com.chalova.irina.todoapp.login.ui

import android.net.Uri

data class LoginState(val isLoading: Boolean = false,
                      val loginUrl: Uri,
                      val authUrlAuthority: String,
                      val redirectUrl: Uri,
                      val loginResult: LoginResult = LoginResult.NotDefined
)