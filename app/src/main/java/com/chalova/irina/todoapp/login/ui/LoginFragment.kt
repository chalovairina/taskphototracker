package com.chalova.irina.todoapp.login.ui

import android.app.Activity
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.config.AppConfig.USER_ID
import com.chalova.irina.todoapp.config.AuthConfig.TOKEN
import com.chalova.irina.todoapp.databinding.FragmentLoginBinding
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.LOGIN_SUCCESSFUL
import com.chalova.irina.todoapp.utils.longToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var backPressDispatcher: OnBackPressedDispatcher
    private var singleBackPress = false

    private val getAuthResponse = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        handleAuthResponse(it.resultCode, it.data)
    }

    private fun handleAuthResponse(resultCode: Int, dataIntent: Intent?) {
        val userId = dataIntent?.getStringExtra(USER_ID)
        val token = dataIntent?.getStringExtra(TOKEN)
        val success = resultCode == Activity.RESULT_OK && userId != null && token != null

        if (success) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(LOGIN_SUCCESSFUL, true)
            authViewModel.onAuthEvent(AuthEvent.Login(userId!!, token!!))
        } else {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(LOGIN_SUCCESSFUL, false)
            longToast(R.string.not_authorized)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressDispatcher()

        binding.vkLoginBtn.setOnClickListener {
            if (networkAvailable()) {
                vkLogin()
            } else {
                longToast(R.string.network_unavailable)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginStatus.collect { loginStatus ->
                    when (loginStatus) {
                        is LoginStatus.LoggedIn -> findNavController().popBackStack()
                        else -> {
                        }
                    }

                }
            }
        }
    }

    private fun networkAvailable(): Boolean {
        val connManager = requireContext().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connManager.getNetworkCapabilities(connManager.activeNetwork) != null
    }

    private fun vkLogin() {
        val intent = Intent(context, WebViewLoginActivity::class.java)
        getAuthResponse.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBackPressDispatcher() {
        backPressDispatcher = requireActivity().onBackPressedDispatcher
        backPressDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (singleBackPress) {
                    requireActivity().finish()
                } else {
                    longToast(R.string.press_again_to_exit)
                    singleBackPress = true

                    Handler(Looper.getMainLooper()).postDelayed({
                        singleBackPress = false
                    }, 2000)
                }
            }
        })
    }
}