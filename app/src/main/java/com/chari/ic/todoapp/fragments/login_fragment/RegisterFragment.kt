package com.chari.ic.todoapp.fragments.login_fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.firebase.MyFireStore
import com.chari.ic.todoapp.firebase.users.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment: AuthFragment() {
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
        savedStateHandle.set(REGISTER_SUCCESSFUL, false)

        registerBtn.setOnClickListener {
            registerUser()
        }
    }


    private fun registerUser() {
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
                    finishFailedRegistration()
                }
        }
    }

    private fun finishSuccessfulRegistration(registeredEmail: String) {
        toDoViewModel.writeUserLoggedIn(true)
        Toast.makeText(requireContext(), String.format(
            getString(R.string.successfully_registered), registeredEmail),
            Toast.LENGTH_LONG).show()

        savedStateHandle.set(CURRENT_USER_ID, usersFirestore.getCurrentUserId())
        findNavController().popBackStack(R.id.introFragment, false)
    }

    private fun finishFailedRegistration() {
        Toast.makeText(requireContext(),
                getString(R.string.registration_failed),
                Toast.LENGTH_SHORT).show()

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
            else -> true
        }
    }
}