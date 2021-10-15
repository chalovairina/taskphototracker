package com.chari.ic.todoapp.fragments.update_fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.databinding.FragmentUpdateBinding
import com.chari.ic.todoapp.fragments.TaskEditFragmentWithBottomSheet
import com.chari.ic.todoapp.utils.PriorityUtils
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant

@AndroidEntryPoint
class UpdateFragment : TaskEditFragmentWithBottomSheet() {
    private val args by navArgs<UpdateFragmentArgs>()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private val toDoViewModel: ToDoViewModel by viewModels()

    private var currentTask: ToDoTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentTask = args.currentTask
        // use copy of the task from recyclerView adapter's list which you want to update
        // so that you are not updating the object using for adapter list viewing
        // and in case if user decided to not apply update and pressed back or up button
        // and returned to recycler view list
        if (currentTask != null) {
            toDoViewModel._taskToUpdate.postValue(currentTask!!.copy())
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = toDoViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currentPrioritySpinner.onItemSelectedListener = listener
        binding.currentDueDateChip.setOnClickListener {
            showBottomSheetDialog(R.id.updateFragment, toDoViewModel)
        }
    }

    override fun onResume() {
        super.onResume()

        showKeyboard(binding.currentTitleEditText)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_save) {
            updateTask()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateTask() {
        val validated = verifyInputData(binding.currentTitleEditText.text.toString())
        if (validated) {
            val currentTask = toDoViewModel.taskToUpdate.value!!
            val updatedTask = createTask(
                currentTask.id,
                currentTask.userId,
                binding.currentTitleEditText.text.toString(),
                binding.currentPrioritySpinner.selectedItem.toString(),
                binding.currentDescriptionEditText.text.toString(),
                currentTask.dueDate,
                currentTask.createdAt,
                currentTask.completed
            )

            toDoViewModel.updateTask(updatedTask)
            makeToast(getString(R.string.successfully_updated))
            findNavController().navigate(R.id.action_updateFragment_to_tasksFragment)
        } else {
            makeToast(getString(R.string.fill_in_title_field))
        }
    }

    private fun createTask(
        id: Int,
        userId: String,
        title: String,
        priority: String,
        description: String,
        dueDate: Instant,
        createdAt: Instant,
        completed: Boolean
    ): ToDoTask {
        return ToDoTask(id, userId, title, PriorityUtils.getPriorityByName(priority),
            description, dueDate, createdAt, completed)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        toDoViewModel._taskToUpdate.postValue(null)
        _binding = null
    }

}