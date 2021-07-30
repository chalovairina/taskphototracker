package com.chari.ic.todoapp.fragments.add_fragment

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.ToDoViewModelFactory
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.ToDoRepository

class AddFragment: Fragment() {
    private lateinit var titleEt: EditText
    private lateinit var descriptionEt: EditText
    private lateinit var prioritySpinner: Spinner

    private val toDoViewModel: ToDoViewModel by lazy {
        ViewModelProvider(
            this,
            ToDoViewModelFactory(
                requireContext().applicationContext as Application,
                ToDoRepository.getRepository()
            ))
            .get(ToDoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        titleEt = view.findViewById(R.id.title_editText)
        descriptionEt = view.findViewById(R.id.description_editText)
        prioritySpinner = view.findViewById(R.id.priority_spinner)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prioritySpinner.onItemSelectedListener = toDoViewModel.listener
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            insertData()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun insertData() {
        val title = titleEt.text.toString()
        val description = descriptionEt.text.toString()
        val priority = prioritySpinner.selectedItem.toString()
        val validated = verifyDataFromUser(title)
        if (validated) {
            val data = ToDoTask(
                0,
                title,
                getPriority(priority),
                description
            )
            toDoViewModel.insertToDoTask(data)
            Toast.makeText(requireContext(), getString(R.string.successfully_added), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_tasksFragment)
        } else {
            Toast.makeText(requireContext(), getString(R.string.empty_data), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPriority(priority: String): Priority {
        return when (priority) {
            "High Priority" -> Priority.HIGH
            "Medium Priority" -> Priority.MEDIUM
            "Low Priority" -> Priority.LOW
            else -> Priority.LOW
        }
    }

    private fun verifyDataFromUser(title: String): Boolean {
        return title.isNotEmpty()
    }
}