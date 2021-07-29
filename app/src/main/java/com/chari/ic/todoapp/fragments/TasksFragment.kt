package com.chari.ic.todoapp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.chari.ic.todoapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {
    private lateinit var addBtn: FloatingActionButton
    private lateinit var tasksListLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_tasks, container, false)
        addBtn = view.findViewById(R.id.add_button)
        tasksListLayout = view.findViewById(R.id.tasks_list_layout)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment_to_addFragment)
        }

        tasksListLayout.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment_to_updateFragment)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }
}