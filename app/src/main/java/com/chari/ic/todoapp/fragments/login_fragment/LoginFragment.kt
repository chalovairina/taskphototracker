package com.chari.ic.todoapp.fragments.login_fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.firebase.MyFireStore
import com.chari.ic.todoapp.firebase.users.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: AuthFragment() {
    @Inject
    lateinit var usersFirestore: MyFireStore

    private val toDoViewModel: ToDoViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_sign_in, container, false)
        emailEditText = view.findViewById(R.id.sign_in_email_editText)
        passwordEditText = view.findViewById(R.id.sign_in_password_editText)
        loginBtn = view.findViewById(R.id.sign_in_btn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SignIn Fragment", "PreviousBackStackEntry = ${findNavController().previousBackStackEntry}")
        Log.d("SignIn Fragment", "CurrentBackStackEntry = ${findNavController().currentBackStackEntry}")

        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle

        loginBtn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        val validated = validateForm(email, password)
        if (validated) {
            showLoadingDialog(getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    hideLoadingDialog()
                    usersFirestore.loadUser()
                        .addOnSuccessListener {
                            val loggedInUser = it.toObject(User::class.java)
                            if (loggedInUser != null) {
                                toDoViewModel.writeCurrentUserData(loggedInUser.id, loggedInUser.name, loggedInUser.mobile,
                                    loggedInUser.image, loggedInUser.email, loggedInUser.fcmToken)
                                finishSuccessfulLogin()
                            } else {
                                finishFailedLogin()
                            }
                        }
                        .addOnFailureListener {
                            finishFailedLogin()
                        }
                }
                .addOnFailureListener {
                    hideLoadingDialog()
                    showToastLong(it.message.toString())
                }
        }
    }

    private fun finishSuccessfulLogin() {
        savedStateHandle.set(CURRENT_USER_ID, usersFirestore.getCurrentUserId())
        toDoViewModel.writeUserLoggedIn(true)

        showToastLong(getString(R.string.successfully_logged_in))

        findNavController().popBackStack(R.id.introFragment, false)
    }

    private fun finishFailedLogin() {
        showToastShort(getString(R.string.authentication_failed))

        findNavController().popBackStack(R.id.introFragment, false)
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(getString(R.string.fill_in_email))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(getString(R.string.fill_in_password))
                false
            }
            else -> true
        }
    }
}