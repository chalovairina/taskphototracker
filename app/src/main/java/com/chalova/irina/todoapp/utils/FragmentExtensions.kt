package com.chalova.irina.todoapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chalova.irina.todoapp.R
import com.google.android.material.snackbar.Snackbar

fun Fragment.shortToast(@StringRes stringRes: Int) {
    Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
}

fun Fragment.longToast(@StringRes stringRes: Int) {
    Toast.makeText(requireContext(), stringRes, Toast.LENGTH_LONG).show()
}

fun Fragment.shortToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.longToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}

fun Fragment.errorSnackBar(@StringRes stringRes: Int) {
    val snackBar = Snackbar.make(
        requireActivity().findViewById(android.R.id.content),
        stringRes,
        Snackbar.LENGTH_LONG
    )
    snackBar.view.setBackgroundColor(
        ContextCompat.getColor(requireContext(), R.color.snackBarErrorColor)
    )
    snackBar.show()
}

fun Fragment.hideKeyboard(view: View) {
    (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.showKeyboard(view: View) {
    view.requestFocus()
    (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.showLongSnackBarWithAction(
    view: View, message: String, @StringRes actionStringRes: Int,
    listener: View.OnClickListener?
) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .setAction(getString(actionStringRes), listener)
        .show()
}