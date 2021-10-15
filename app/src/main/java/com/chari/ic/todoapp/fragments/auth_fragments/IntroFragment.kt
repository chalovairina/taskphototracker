package com.chari.ic.todoapp.fragments.auth_fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.chari.ic.todoapp.R
import dagger.hilt.android.AndroidEntryPoint

const val CURRENT_USER_ID: String = "CURRENT_USER_ID"
@AndroidEntryPoint
class IntroFragment: Fragment() {
    private lateinit var signUpBtn: Button
    private lateinit var signInBtn: Button

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

        signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_registerFragment)
        }
        signInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }

        val navController = findNavController()

        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<String>(CURRENT_USER_ID)
            .observe(currentBackStackEntry, { userId ->
                if (userId.isNotEmpty()) {
                    val startDestination = navController.graph.startDestinationId
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.introFragment, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            })
    }

}