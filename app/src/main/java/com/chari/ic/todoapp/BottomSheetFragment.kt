package com.chari.ic.todoapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.databinding.BottomSheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

const val BOTTOM_SHEET_DIALOG = "BottomSheetDialog"
private const val KEY_CONTEXT_FRAGMENT_ID = "FragmentId"
class BottomSheetFragment: BottomSheetDialogFragment(), OnClickListener {
    private lateinit var toDoViewModel: ToDoViewModel

    private var selectedPriorityButtonId: Int = -1

    private var _binding: BottomSheetFragmentBinding? = null
    private val binding get() = _binding!!

    private var dueDate: Instant? = null

    private var priority: Priority? = null

    private var fragmentId: Int = -1

    private var taskToUpdate: ToDoTask? = null

    // put id of the fragment where dialog is called into bottomSheetDialogtFragment arguments
    companion object {
        fun getInstance(fragmentId: Int, toDoViewModel: ToDoViewModel): BottomSheetFragment {
            val args = Bundle().apply {
                putSerializable(KEY_CONTEXT_FRAGMENT_ID, fragmentId)

            }
            return BottomSheetFragment().apply {
                this.toDoViewModel = toDoViewModel
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentId = if (arguments != null) arguments!![KEY_CONTEXT_FRAGMENT_ID] as Int else -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVisibilityForContextFragment()

        if (binding.saveImageView.visibility != GONE) {
            binding.saveImageView.setOnClickListener {
                hideKeyboard(binding.bottomSheet)
                setupSaveButton()
            }
        }

        if (binding.dialogCloseImageView.visibility != GONE) {
            binding.dialogCloseImageView.setOnClickListener {
                hideKeyboard(binding.bottomSheet)
                setupDialogCloseButton()
            }
        }

        binding.calendarImageView.setOnClickListener {
            hideKeyboard(binding.bottomSheet)
            setupCalendarButton()
        }

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            dueDate = LocalDate.of(year, month, dayOfMonth)
                .atStartOfDay()
                .toInstant(
                    ZoneOffset.systemDefault()
                        .rules.getOffset(Instant.now())
                )
        }

        binding.priorityImageView.setOnClickListener {
            hideKeyboard(binding.bottomSheet)
            setupPriorityButton()
        }

        binding.todayChip.setOnClickListener(this)
        binding.tomorrowChip.setOnClickListener(this)
        binding.nextWeekChip.setOnClickListener(this)
    }

    private fun setupSaveButton() {
        val taskTitle = binding.bottomTitleEditText.text.toString().trim()
        if (taskTitle.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.fill_in_title_field), Toast.LENGTH_LONG)
                .show()
        }
        if (dueDate == null) {
            Toast.makeText(requireContext(),
                getString(R.string.choose_due_date),
                Toast.LENGTH_LONG)
                .show()
        }
        if (priority == null) {
            Toast.makeText(requireContext(),
                "Please choose task priority with flag menu",
                Toast.LENGTH_LONG)
                .show()
        }

        if (taskTitle.isNotEmpty() && dueDate != null && priority != null) {
            val newTask = ToDoTask(
                0,
                FirebaseAuth.getInstance().currentUser!!.uid,
                taskTitle,
                priority!!,
                "",
                dueDate!!,
                Instant.now(),
                false
            )
            toDoViewModel.insertTask(newTask)
            Toast.makeText(requireContext(),
                getString(R.string.successfully_added), Toast.LENGTH_SHORT)
                .show()
            dismiss()
        }
    }

    private fun setupDialogCloseButton() {
        taskToUpdate = toDoViewModel.taskToUpdate.value
        var changesApplied = false
        if (priority != null) {
            changesApplied = true
            taskToUpdate?.priority = priority!!
        }
        if (dueDate != null) {
            changesApplied = true
            taskToUpdate?.dueDate = dueDate!!
        }
        if (taskToUpdate != null && changesApplied) {
            toDoViewModel._taskToUpdate.postValue(taskToUpdate)
        }

        this.dismiss()
    }

    private fun setupCalendarButton() {
        if (binding.calendarGroup.visibility == VISIBLE) {
            binding.calendarGroup.visibility = GONE
        } else {
            binding.calendarGroup.visibility = VISIBLE
        }
    }

    private fun setupPriorityButton() {
        if (binding.priorityRadioGroup.visibility == VISIBLE) {
            binding.priorityRadioGroup.visibility = GONE
        } else {
            binding.priorityRadioGroup.visibility = VISIBLE
        }

        binding.priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.radioButton_high -> priority = Priority.HIGH
                R.id.radioButton_med -> priority = Priority.MEDIUM
                R.id.radioButton_low -> priority = Priority.LOW
            }

        }
    }

    /**
     * Sets visibility for bottomSheet views based on fragment id passed in arguments bundle
     * when BottomSheetDialogFragment instantiation called by different app fragments.
     */
    private fun setVisibilityForContextFragment() {
        if (fragmentId != -1) {
            when(fragmentId) {
                R.id.updateFragment -> {
                    binding.addNewTaskTextView.visibility = GONE
                    binding.saveImageView.visibility = GONE
                    binding.bottomTitleEditText.visibility = GONE
                    binding.dialogCloseImageView.visibility = VISIBLE
                }
                R.id.tasksFragment -> {
                    binding.dialogCloseImageView.visibility = GONE
                }
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.today_chip -> dueDate = Instant.now()
            R.id.tomorrow_chip -> dueDate = Instant.now().plus(1, ChronoUnit.DAYS)
            R.id.next_week_chip -> dueDate = Instant.now().plus(7, ChronoUnit.DAYS)
        }
        binding.calendarGroup.visibility = GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun hideKeyboard(view: View) {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        view.requestFocus()
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}