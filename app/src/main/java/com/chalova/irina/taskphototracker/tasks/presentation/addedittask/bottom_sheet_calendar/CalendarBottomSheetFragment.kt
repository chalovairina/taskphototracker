package com.chalova.irina.taskphototracker.tasks.presentation.addedittask.bottom_sheet_calendar

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chalova.irina.taskphototracker.MainActivity
import com.chalova.irina.taskphototracker.R
import com.chalova.irina.taskphototracker.databinding.FragmentBottomSheetCalendarBinding
import com.chalova.irina.taskphototracker.di.provideBottomCalendarFactory
import com.chalova.irina.taskphototracker.tasks.data.util.DateTimeUtil
import com.chalova.irina.taskphototracker.tasks.presentation.utils.NavigationArgs.TASK_DUE_DATE
import com.chalova.irina.taskphototracker.utils.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CalendarBottomSheetFragment : BottomSheetDialogFragment(), OnClickListener {

    private var _binding: FragmentBottomSheetCalendarBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<CalendarBottomSheetFragmentArgs>()

    private lateinit var lifecycleOwner: NavBackStackEntry

    @Inject
    lateinit var viewModelFactory: BottomCalendarViewModel.BottomCalendarViewModelFactory
    private val bottomCalendarViewModel: BottomCalendarViewModel by viewModels {
        provideBottomCalendarFactory(
            viewModelFactory,
            findNavController().currentBackStackEntry!!.savedStateHandle
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity)
            .activityComponent
            .bottomCalendarFragmentComponentFactory().create()
            .inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            it[TASK_DUE_DATE] = args.dueDate
        }

        return super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0.3f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleOwner = findNavController().getBackStackEntry(R.id.bottomSheetCalendarDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBottomSheetCalendarBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupUiState()
    }

    private fun setupUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            bottomCalendarViewModel.dueDateState.flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            ).collect { state ->
                binding.calendarView.date = DateTimeUtil.toInstant(state.dueDate).toEpochMilli()
            }
        }
    }

    private fun setupUI() {
        binding.saveImageView.setOnClickListener {
            hideKeyboard(binding.bottomSheet)
            saveChangesAndClose()
        }

        binding.dialogCloseImageView.setOnClickListener {
            hideKeyboard(binding.bottomSheet)
            closeDialog()
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            bottomCalendarViewModel.dateUpdated(year, month + 1, dayOfMonth)
        }

        binding.todayChip.setOnClickListener(this)
        binding.tomorrowChip.setOnClickListener(this)
        binding.nextWeekChip.setOnClickListener(this)
    }

    private fun saveChangesAndClose() {
        parentFragment?.id?.let {
            findNavController().getBackStackEntry(R.id.addEditFragment)
                .savedStateHandle[TASK_DUE_DATE] =
                bottomCalendarViewModel.dueDateState.value.dueDate.toString()
        }

        closeDialog()
    }

    private fun closeDialog() {
        dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.today_chip -> bottomCalendarViewModel.dateUpdated(
                Instant.now()
            )
            R.id.tomorrow_chip -> bottomCalendarViewModel.dateUpdated(
                Instant.now().plus(1, ChronoUnit.DAYS)
            )
            R.id.next_week_chip -> bottomCalendarViewModel.dateUpdated(
                Instant.now().plus(7, ChronoUnit.DAYS)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}