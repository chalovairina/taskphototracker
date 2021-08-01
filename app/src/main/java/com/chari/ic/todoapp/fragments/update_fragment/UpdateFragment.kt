package com.chari.ic.todoapp.fragments.update_fragment

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoApplication
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.ToDoViewModelFactory
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.fragments.TaskEditFragment
import com.chari.ic.todoapp.repository.ToDoRepository

class UpdateFragment : TaskEditFragment() {
    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var currentTask: ToDoTask

    private val toDoViewModel by viewModels<ToDoViewModel> {
            ToDoViewModelFactory(
//                requireContext().applicationContext as Application,
                ToDoRepository.getRepository()
            )
    }

    private lateinit var currentTitle: EditText
    private lateinit var currentDescription: EditText
    private lateinit var priorityIndicator: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentTask = args.currentTask
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update, container, false)
        currentTitle = view.findViewById(R.id.current_title_editText)
        currentDescription = view.findViewById(R.id.current_description_editText)
        priorityIndicator = view.findViewById(R.id.current_priority_spinner)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTitle.setText(currentTask.title)
        currentDescription.setText(currentTask.description)

        priorityIndicator.onItemSelectedListener = listener

        priorityIndicator.setSelection(getPositionByPriority(currentTask.priority))
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
        val id =  currentTask.id
        val title = currentTitle.text.toString()
        val description = currentDescription.text.toString()
        val priority = priorityIndicator.selectedItem.toString()
        val validated = verifyTitle(title)
        if (validated) {
            val updatedTask = ToDoTask(
                id,
                title,
                getPriorityByName(priority),
                description
            )
            toDoViewModel.updateTask(updatedTask)
            makeToast(getString(R.string.successfully_updated))
            findNavController().navigate(R.id.action_updateFragment_to_tasksFragment)
        } else {
            makeToast(getString(R.string.fill_in_title_field))
        }
    }

}