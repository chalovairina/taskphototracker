package com.chalova.irina.todoapp.tasks.presentation.addedittask

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.databinding.FragmentAddEditTaskBinding
import com.chalova.irina.todoapp.di.provideAddEditFactory
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import com.chalova.irina.todoapp.tasks.data.util.Priority
import com.chalova.irina.todoapp.tasks.presentation.utils.NavigationArgs.CURRENT_TASK_ID
import com.chalova.irina.todoapp.tasks.presentation.utils.NavigationArgs.TASK_DUE_DATE
import com.chalova.irina.todoapp.utils.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditTaskFragment : Fragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<AddEditTaskFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: AddEditViewModel.AddEditViewModelFactory
    private val addEditViewModel: AddEditViewModel by viewModels {
        provideAddEditFactory(
            viewModelFactory,
            findNavController().currentBackStackEntry!!.savedStateHandle
        )
    }

    private val menuHost: MenuHost get() = requireActivity()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity)
            .activityComponent
            .addEditFragmentComponentFactory().create()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            it[CURRENT_TASK_ID] = args.currentTaskId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTaskBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupUI()

        retrieveBottomSheetCalendarData()

        setupUIState()
    }

    private fun setupMenu() {
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_add_edit, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_save) {
                    saveTask()
                }

                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setupUI() {
        // title
        binding.currentTitleEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    addEditViewModel.onEvent(AddEditTaskEvent.TitleChanged(s.toString()))
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        binding.currentTitleEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard(v)
            } else {
                hideKeyboard(v)
            }
        }

        // description
        binding.currentDescriptionEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    addEditViewModel.onEvent(AddEditTaskEvent.DescriptionChanged(s.toString()))
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        binding.currentDescriptionEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard(v)
            } else {
                hideKeyboard(v)
            }
        }

        // priority
        binding.currentPrioritySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (view as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            PriorityUtils.getColorByPriorityPosition(position)
                        )
                    )
                    addEditViewModel.onEvent(
                        AddEditTaskEvent.PriorityChanged(
                            PriorityUtils.getPriorityByPriorityPosition(position)
                        )
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // due date
        binding.currentDueDateChip.setOnClickListener {
            openBottomSheetCalendar()
        }
    }

    private fun setupUIState() {
        viewLifecycleOwner.repeatOnState(Lifecycle.State.STARTED) {
            launch {
                addEditViewModel.userMessage.collect { message ->
                    shortToast(message)
                }
            }
            launch {
                addEditViewModel.addEditTaskState.collect { state ->
                    if (state.isTaskSaved) {
                        findNavController().popBackStack()
                    }
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: AddEditTaskState) {
        setTextFields(state)
        updateHints(state)
        setDate(state)
        setPriority(state)
    }

    private fun setTextFields(state: AddEditTaskState) {
        if (binding.currentTitleEditText.text.toString() != state.titleState.title)
            binding.currentTitleEditText.setText(state.titleState.title)
        if (binding.currentDescriptionEditText.text.toString() != state.descriptionState.description)
            binding.currentDescriptionEditText.setText(state.descriptionState.description)
    }

    private fun setPriority(state: AddEditTaskState) {
        binding.currentPrioritySpinner.setSelection(
            when (state.priorityState.priority) {
                Priority.HIGH -> PriorityUtils.PRIORITY_POSITION_HIGH
                Priority.MEDIUM -> PriorityUtils.PRIORITY_POSITION_MEDIUM
                Priority.LOW -> PriorityUtils.PRIORITY_POSITION_LOW
            }
        )
    }

    private fun setDate(state: AddEditTaskState) {
        binding.currentDueDateChip.text = DateTimeUtil.formatDate(
            DateTimeUtil.toInstant(
                state.dueDateState.dueDate
            )
        )
    }

    private fun updateHints(state: AddEditTaskState) {
        setTitleHint(state)
        setDueDateHint(state)
    }

    private fun setTitleHint(state: AddEditTaskState) {
        state.titleState.error?.let {
            binding.titleHint.apply {
                text = when (it) {
                    is TaskState.Error.EmptyValue -> getString(R.string.addedit_empty_title)
                    else -> getString(R.string.addedit_title_hint)
                }
                setTextColor(ContextCompat.getColor(context, R.color.venetian_red))
            }
        } ?: run {
            binding.titleHint.text = getString(R.string.addedit_title_hint)
        }
    }

    private fun setDueDateHint(state: AddEditTaskState) {
        state.dueDateState.error?.let {
            binding.dateHint.apply {
                text = when (it) {
                    is TaskState.Error.PastDate -> getString(R.string.addedit_past_date)
                    else -> getString(R.string.addedit_choose_due_date)
                }
                setTextColor(ContextCompat.getColor(context, R.color.venetian_red))
            }
        } ?: run {
            binding.dateHint.text = getString(R.string.addedit_choose_due_date)
        }
    }

    private fun retrieveBottomSheetCalendarData() {
        val navBackStackEntry: NavBackStackEntry = findNavController()
            .getBackStackEntry(R.id.addEditFragment)

        navBackStackEntry.repeatOnState(Lifecycle.State.STARTED) {
            navBackStackEntry.savedStateHandle.getStateFlow<String?>(TASK_DUE_DATE, null)
                .collect { dueDate ->
                    dueDate?.let {
                        addEditViewModel.onEvent(
                            AddEditTaskEvent.DueDateChanged(
                                DateTimeUtil.toLocalDate(it)
                            )
                        )
                    }
                }
        }
    }

    private fun openBottomSheetCalendar() {
        findNavController().navigate(
            AddEditTaskFragmentDirections.actionAddEditFragmentToBottomSheetCalendarDialog(
                addEditViewModel.addEditTaskState.value.dueDateState.dueDate.toString()
            )
        )
    }

    private fun saveTask() {
        addEditViewModel.onEvent(AddEditTaskEvent.SaveAddEditTask)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}