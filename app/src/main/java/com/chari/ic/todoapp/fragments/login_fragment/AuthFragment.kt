package com.chari.ic.todoapp.fragments.login_fragment

import androidx.core.content.ContextCompat
import com.chari.ic.todoapp.R
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class AuthFragment: ProgressWaitingFragment() {
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