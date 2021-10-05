package com.chari.ic.todoapp.fragments.login_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chari.ic.todoapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ProgressWaitingFragment: Fragment() {
    private lateinit var loadingDialog: Dialog
    private lateinit var loadingTextView: TextView

    protected fun showLoadingDialog(text: String) {
        loadingTextView.text = text
        loadingDialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(R.layout.loading_dialog)
        loadingTextView = loadingDialog.findViewById(R.id.dialog_text)
    }

    protected fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }

    protected fun showToastLong(message: String) {
        Toast.makeText(requireContext(), message,
            Toast.LENGTH_LONG).show()
    }

    protected fun showToastShort(message: String) {
        Toast.makeText(requireContext(), message,
            Toast.LENGTH_SHORT).show()
    }
}