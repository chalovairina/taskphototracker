package com.chari.ic.todoapp.fragments.auth_fragments

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
import com.chari.ic.todoapp.utils.idling_resource.idling_resource_with_callback.RegisterIdlingResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment: AuthFragment() {
    private val EMAIL_PATTERN = ("^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$")

    private val pattern = Pattern.compile(EMAIL_PATTERN)
    private lateinit var matcher: Matcher

    @Inject lateinit var usersFirestore: MyFireStore
    private lateinit var auth: FirebaseAuth

    private val toDoViewModel: ToDoViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var userNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerBtn: Button

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
        val view =  inflater.inflate(R.layout.fragment_sign_up, container, false)
        userNameEditText = view.findViewById(R.id.sign_up_username_editText)
        emailEditText = view.findViewById(R.id.sign_up_email_editText)
        passwordEditText = view.findViewById(R.id.sign_up_password_editText)
        registerBtn = view.findViewById(R.id.sign_up_btn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle

        registerBtn.setOnClickListener {
            registerUser()
        }
    }


    private fun registerUser() {
        RegisterIdlingResource.setIdleState(false)
        val userName = userNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        val validated = validateForm(userName, email, password)

        if (validated) {
            showLoadingDialog(getString(R.string.please_wait))
            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    hideLoadingDialog()
                    val registeredUser: FirebaseUser? = it.user
                    val registeredEmail = registeredUser?.email!!
                    if (registeredUser != null) {
                        val user = User(registeredUser.uid, userName, registeredEmail)
                        usersFirestore.registerUser(user)
                            .addOnSuccessListener {
                                toDoViewModel.writeCurrentUserData(user.id, user.name, user.mobile,
                                user.image, user.email, user.fcmToken)
                                finishSuccessfulRegistration(registeredEmail)
                            }
                            .addOnFailureListener {
                                finishFailedRegistration()
                            }
                    } else {
                        finishFailedRegistration()
                    }
                }
                .addOnFailureListener {
                    hideLoadingDialog()
                    Log.d("Register Fragment:", it.message.toString())

                    finishFailedRegistration()
                }
        }
    }

    private fun finishSuccessfulRegistration(registeredEmail: String) {
        savedStateHandle.set(CURRENT_USER_ID, usersFirestore.getCurrentUserId())

        showToastLong(
            String.format(getString(R.string.successfully_registered), registeredEmail)
        )

        findNavController().popBackStack(R.id.introFragment, false)
    }

    private fun finishFailedRegistration() {
        showToastShort(getString(R.string.registration_failed))

        findNavController().popBackStack(R.id.introFragment, false)
    }

    private fun validateForm(userName: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(userName) -> {
                showErrorSnackBar(getString(R.string.fill_in_username))
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(getString(R.string.fill_in_email))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(getString(R.string.fill_in_password))
                false
            }
            !validateEmail(email) -> {
                showErrorSnackBar(getString(R.string.email_from_incorrect))
                false
            }
            !validatePassword(password) -> {
                showErrorSnackBar(getString(R.string.password_not_strong_enough))
                false
            }
            else -> true
        }
    }

    protected fun validateEmail(email: String): Boolean {
        matcher = pattern.matcher(email)

        return matcher.matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >=8 && Regex("^(?=.*[0-9])").containsMatchIn(password)
                && Regex("^(?=.*[A-Z])").containsMatchIn(password)
                && Regex("^(?=.*[~!@#-$%^&*_+=`|(){}\\[\\]:;\"'<>,\\.\\?/\\\\])").containsMatchIn(password)
    }
}