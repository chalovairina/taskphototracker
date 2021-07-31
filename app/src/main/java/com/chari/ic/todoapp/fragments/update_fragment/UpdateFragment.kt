package com.chari.ic.todoapp.fragments.update_fragment

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.ToDoViewModelFactory
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.fragments.TaskEditFragment
import com.chari.ic.todoapp.repository.ToDoRepository
import com.chari.ic.todoapp.utils.Constants

class UpdateFragment : TaskEditFragment() {
    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var currentTask: ToDoTask

    private val toDoViewModel: ToDoViewModel by lazy {
        ViewModelProvider(
            this,
            ToDoViewModelFactory(
                requireContext().applicationContext as Application,
                ToDoRepository.getRepository()
            )
        )
            .get(ToDoViewModel::class.java)
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

        priorityIndicator.setSelection(getPriorityPosition(currentTask.priority))
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
        val validated = verifyDataFromUser(title)
        if (validated) {
            val updatedTask = ToDoTask(
                id,
                title,
                getPriority(priority),
                description
            )
            toDoViewModel.updateToDoTask(updatedTask)
            makeToast(getString(R.string.successfully_updated))
            findNavController().navigate(R.id.action_updateFragment_to_tasksFragment)
        } else {
            makeToast(getString(R.string.fill_in_title_field))
        }
    }

}