package com.chalova.irina.todoapp.login_auth.presentation.login

import android.app.Activity
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.databinding.FragmentLoginBinding
import com.chalova.irina.todoapp.tasks.presentation.tasks.TasksFragmentDirections
import com.chalova.irina.todoapp.utils.longToast
import timber.log.Timber

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var backPressDispatcher: OnBackPressedDispatcher
    private var singleBackPress = false

    private val getAuthResponse = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        handleAuthResponse(it.resultCode, it.data)
    }

    private fun handleAuthResponse(resultCode: Int, dataIntent: Intent?) {
        val success = resultCode == Activity.RESULT_OK

        if (success) {
            findNavController().navigate(TasksFragmentDirections.actionGlobalStartDestination())
        } else {
            longToast(R.string.login_not_authorized)
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
                longToast(R.string.login_network_unavailable)
            }
        }
    }

    private fun networkAvailable(): Boolean {
        val connManager =
            requireContext().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
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
                Timber.d("handleOnBackPressed")
                if (singleBackPress) {
                    requireActivity().finish()
                } else {
                    longToast(R.string.login_press_again_to_exit)
                    singleBackPress = true

                    Handler(Looper.getMainLooper()).postDelayed({
                        singleBackPress = false
                    }, 2000)
                }
            }
        })
    }
}