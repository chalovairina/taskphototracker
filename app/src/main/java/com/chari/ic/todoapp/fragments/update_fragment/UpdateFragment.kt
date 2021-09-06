package com.chari.ic.todoapp.fragments.update_fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.databinding.FragmentUpdateBinding
import com.chari.ic.todoapp.fragments.TaskEditFragment
import com.chari.ic.todoapp.utils.PriorityUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateFragment : TaskEditFragment() {
    private val args by navArgs<UpdateFragmentArgs>()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentTask: ToDoTask

    private val toDoViewModel: ToDoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentTask = args.currentTask
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.task = currentTask

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currentPrioritySpinner.onItemSelectedListener = listener
    }

    override fun onResume() {
        super.onResume()
        Log.d("UpdateFragment", "onResume")

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
            val updatedTask = createTask(
                currentTask.id,
                binding.currentTitleEditText.text.toString(),
                binding.currentPrioritySpinner.selectedItem.toString(),
                binding.currentDescriptionEditText.text.toString()
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
        title: String,
        priority: String,
        description: String
    ): ToDoTask {
        return ToDoTask(id, title, PriorityUtils.getPriorityByName(priority), description)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}