package com.chari.ic.todoapp.fragments

import androidx.fragment.app.Fragment
import com.chari.ic.todoapp.BOTTOM_SHEET_DIALOG
import com.chari.ic.todoapp.BottomSheetFragment
import com.chari.ic.todoapp.ToDoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class FragmentWithModalBottomSheetDialog: Fragment() {
    private var bottomSheetDialogFragment: BottomSheetDialogFragment? = null

    protected fun showBottomSheetDialog(fragmentId: Int, toDoViewModel: ToDoViewModel) {
        instantiateBottomSheetFragment(fragmentId, toDoViewModel)
        bottomSheetDialogFragment!!.show(this@FragmentWithModalBottomSheetDialog.parentFragmentManager, BOTTOM_SHEET_DIALOG)
    }

    private fun instantiateBottomSheetFragment(fragmentId: Int, toDoViewModel: ToDoViewModel) {
        bottomSheetDialogFragment = BottomSheetFragment.getInstance(fragmentId, toDoViewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        bottomSheetDialogFragment = null
    }
}