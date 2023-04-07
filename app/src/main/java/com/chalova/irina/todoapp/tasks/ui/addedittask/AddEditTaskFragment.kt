package com.chalova.irina.todoapp.tasks.ui.addedittask

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.databinding.FragmentAddEditTaskBinding
import com.chalova.irina.todoapp.tasks.data.util.DateTimeUtil
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.CURRENT_TASK_ID
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.TASK_DUE_DATE
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.USER_ID
import com.chalova.irina.todoapp.utils.PriorityUtils
import com.chalova.irina.todoapp.utils.hideKeyboard
import com.chalova.irina.todoapp.utils.showKeyboard
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditTaskFragment: Fragment() {

    private lateinit var binding: FragmentAddEditTaskBinding

    val args by navArgs<AddEditTaskFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: AddEditViewModel.AddEditViewModelFactory
    private val addEditViewModel: AddEditViewModel by viewModels {
        provideAddEditFactory(viewModelFactory, findNavController().currentBackStackEntry!!.savedStateHandle)
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
            it[USER_ID] = args.userId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_edit_task, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = addEditViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupViews()

        getBottomSheetCalendarData()

        viewLifecycleOwner.lifecycleScope.launch{
            addEditViewModel.isTaskSaved.flowWithLifecycle(viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED).collect { isTaskSaved ->
                if (isTaskSaved) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setupViews() {
        // title
        binding.currentTitleEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int
                ) {}

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
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int
                ) {}

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
        binding.currentPrioritySpinner.setOnItemSelectedListener(
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
            })

        // due date
        binding.currentDueDateChip.setOnClickListener {
            openBottomSheetCalendar()
        }
    }

    private fun getBottomSheetCalendarData() {
        val navBackStackEntry: NavBackStackEntry = findNavController().getBackStackEntry(R.id.addEditFragment)
        navBackStackEntry.savedStateHandle
            .getLiveData<String>(TASK_DUE_DATE).observe(navBackStackEntry) { dueDate ->
            addEditViewModel.onEvent(AddEditTaskEvent.DueDateChanged(
                DateTimeUtil.toLocalDate(dueDate)
            ))
        }
    }

    private fun setupMenu() {
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_save) {
                    saveTask()
                }

                return true
            }
        }, viewLifecycleOwner)
    }

    private fun openBottomSheetCalendar() {
        findNavController().navigate(
            AddEditTaskFragmentDirections.
            actionAddEditFragmentToBottomSheetCalendarDialog(
                addEditViewModel.dueDateState.value.dueDate.toString())
        )
    }

    private fun saveTask() {
        addEditViewModel.onEvent(AddEditTaskEvent.SaveAddEditTask)
    }

}