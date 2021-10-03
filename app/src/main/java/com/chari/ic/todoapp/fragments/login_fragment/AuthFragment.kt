package com.chari.ic.todoapp.fragments.login_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chari.ic.todoapp.R
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
open class AuthFragment: Fragment() {
    private lateinit var loadingDialog: Dialog

    protected fun showLoadingDialog(text: String) {
        loadingDialog.findViewById<TextView>(R.id.dialog_text).text = text
        loadingDialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(R.layout.loading_dialog)
    }

    protected fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }

    private val EMAIL_PATTERN = ("^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$")

    private val pattern = Pattern.compile(EMAIL_PATTERN)
    private lateinit var matcher: Matcher

    protected fun validateEmail(email: String): Boolean {
        matcher = pattern.matcher(email)

        return matcher.matches()
    }

    protected fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        )
        snackBar.view.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.snackBarErrorColor)
        )
        snackBar.show()
    }
}