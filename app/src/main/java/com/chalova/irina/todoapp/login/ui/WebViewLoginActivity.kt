package com.chalova.irina.todoapp.login.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.ToDoApplication
import com.chalova.irina.todoapp.config.AppConfig.USER_ID
import com.chalova.irina.todoapp.config.AuthConfig.TOKEN
import com.chalova.irina.todoapp.databinding.ActivityAuthBinding
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.LOGIN_SUCCESSFUL
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebViewLoginActivity: AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var webClient: AuthWebViewClient

    private lateinit var binding: ActivityAuthBinding

    private lateinit var loginViewModel: LoginViewModel
    @Inject lateinit var viewModelFactory: LoginViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as ToDoApplication).appComponent
            .loginComponentFactory()
            .create().inject(this)

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)

        webView = binding.webView

        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewmodel = loginViewModel

        setWebView()

        lifecycleScope.launch {
            loginViewModel.loginState.flowWithLifecycle(
                lifecycle, Lifecycle.State.STARTED).collect { loginState ->
                when (loginState.loginResult) {
                    is LoginResult.Success -> {
                        intent.putExtra(USER_ID, loginState.loginResult.userId)
                        intent.putExtra(TOKEN, loginState.loginResult.token)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    is LoginResult.Failed -> {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                    else -> {
                    }
                }
            }
        }

        loadUrl(loginViewModel.loginState.value.loginUrl)
    }

    private fun loadUrl(authUrl: Uri) {
        try {
            webView.loadUrl(authUrl.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            loginViewModel.onLoginEvent(LoginEvent.LoadFailed())
        }
    }

    private fun setWebView() {
        webView.apply {
            webView.clearCache(true)
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
            isVerticalScrollBarEnabled = false
            visibility = View.INVISIBLE
            overScrollMode = View.OVER_SCROLL_NEVER
            webClient = AuthWebViewClient()
            webView.webViewClient = webClient
        }
        webView.settings.apply {
            javaScriptEnabled = false
        }
    }

    override fun onDestroy() {
        webView.destroy()

        super.onDestroy()
    }

    inner class AuthWebViewClient:
        WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            request?.let { req ->
                loginViewModel.onLoginEvent(LoginEvent.UrlLoadStart(req.url.toString()))
                return (!req.url.toString().contains(
                    loginViewModel.loginState.value.authUrlAuthority))
            } ?: return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            url?.let {
                loginViewModel.onLoginEvent(LoginEvent.UrlLoaded(url))
            }
        }
    }
}