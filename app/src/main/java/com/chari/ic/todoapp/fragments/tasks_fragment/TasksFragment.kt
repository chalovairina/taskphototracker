package com.chari.ic.todoapp.fragments.tasks_fragment

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.ToDoViewModelFactory
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.repository.ToDoRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {
    val toDoViewModel: ToDoViewModel by lazy {
        ViewModelProvider(
            this,
            ToDoViewModelFactory(
                requireContext().applicationContext as Application,
                ToDoRepository.getRepository()
            ))
            .get(ToDoViewModel::class.java)
    }

    private lateinit var addBtn: FloatingActionButton
    private lateinit var tasksListLayout: ConstraintLayout
    private lateinit var recyclerView: RecyclerView

    private object TODO_TASKS_DIFF_UTIL: DiffUtil.ItemCallback<ToDoTask>() {
        override fun areItemsTheSame(oldItem: ToDoTask, newItem: ToDoTask): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDoTask, newItem: ToDoTask): Boolean {
            return oldItem.description == newItem.description &&
                    oldItem.title == newItem.title &&
                    oldItem.priority == newItem.priority
        }
    }

    private val adapter: ToDoTaskAdapter by lazy {
        ToDoTaskAdapter(
            requireActivity(),
            toDoViewModel,
            TODO_TASKS_DIFF_UTIL
        ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_tasks, container, false)
        addBtn = view.findViewById(R.id.add_button)
        tasksListLayout = view.findViewById(R.id.tasks_list_layout)
        recyclerView = view.findViewById(R.id.recyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()

        toDoViewModel.getAllData.observe(viewLifecycleOwner) {
            data ->
            if (!data.isNullOrEmpty()) {
                adapter.submitList(data)
            }
        }

        addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment_to_addFragment)
        }
    }

    private fun setupAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        adapter.clearContextualActionMode()
    }
}