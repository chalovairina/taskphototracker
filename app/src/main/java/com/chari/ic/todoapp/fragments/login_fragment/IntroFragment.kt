package com.chari.ic.todoapp.fragments.login_fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import dagger.hilt.android.AndroidEntryPoint

const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
const val REGISTER_SUCCESSFUL: String = "REGISTER_SUCCESSFUL"
const val CURRENT_USER_ID: String = "CURRENT_USER_ID"
@AndroidEntryPoint
class IntroFragment: Fragment() {
    private var currentUserId = ""
    private lateinit var signUpBtn: Button
    private lateinit var signInBtn: Button
    private var backPressedOnce = false

    private val toDoViewModel: ToDoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_intro, container, false)
        signUpBtn = view.findViewById(R.id.sign_up_btn_intro)
        signInBtn = view.findViewById(R.id.sign_in_btn_intro)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Intro Fragment", "PreviousBackStackEntry = ${findNavController().previousBackStackEntry}")
        Log.d("Intro Fragment", "CurrentBackStackEntry = ${findNavController().currentBackStackEntry}")

        signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_registerFragment)
        }
        signInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }

        Log.d("Intro Frag", "intermediate log for check")

        val navController = findNavController()

        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<String>(CURRENT_USER_ID)
            .observe(currentBackStackEntry, Observer { userId ->
                if (userId.isNotEmpty()) {
                    Log.d("Intro Fragment", "login success: PreviousBackStackEntry = ${findNavController().previousBackStackEntry}")
                    Log.d("Intro Fragment", "login success: CurrentBackStackEntry = ${findNavController().currentBackStackEntry}")
                    val startDestination = navController.graph.startDestinationId
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.introFragment, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            })

    }

}