package com.chari.ic.todoapp.fragments.add_fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.ToDoViewModelFactory
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.fragments.TaskEditFragment
import com.chari.ic.todoapp.repository.ToDoRepository
import com.chari.ic.todoapp.utils.PriorityUtils.getPriorityByName

class AddFragment: TaskEditFragment() {
    private lateinit var titleEt: EditText
    private lateinit var descriptionEt: EditText
    private lateinit var prioritySpinner: Spinner

    private val toDoViewModel by viewModels<ToDoViewModel> {
        ToDoViewModelFactory(
            ToDoRepository.getRepository()
        )
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

        prioritySpinner.onItemSelectedListener = listener
    }

    override fun onResume() {
        super.onResume()
        Log.d("AddFragment", "onResume")

        showKeyboard(titleEt)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
//            hideKeyboard(requireView())
            insertData()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun insertData() {
        val title = titleEt.text.toString()
        val description = descriptionEt.text.toString()
        val priority = prioritySpinner.selectedItem.toString()
        val validated = verifyInputData(title)
        if (validated) {
            val data = ToDoTask(
                0,
                title,
                getPriorityByName(priority),
                description
            )
            toDoViewModel.insertTask(data)
            makeToast( getString(R.string.successfully_added))
            findNavController().navigate(R.id.action_addFragment_to_tasksFragment)
        } else {
            makeToast(getString(R.string.fill_in_title_field))
        }
    }
}