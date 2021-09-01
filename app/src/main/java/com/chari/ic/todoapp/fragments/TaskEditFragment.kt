package com.chari.ic.todoapp.fragments

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.utils.PriorityUtils

open class TaskEditFragment: Fragment() {

    val listener: AdapterView.OnItemSelectedListener = object: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            (view as TextView).setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    PriorityUtils.getColorByPriorityPosition(position)
                )
            )
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    protected fun verifyInputData(title: String): Boolean {
        return title.isNotEmpty()
    }

    protected fun makeToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard(view: View) {
        (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun showKeyboard(view: View) {
        view.requestFocus()
        (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onStop() {
        super.onStop()

        view?.let { hideKeyboard(it) }
    }
}