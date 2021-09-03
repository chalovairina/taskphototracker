package com.chari.ic.todoapp.fragments.tasks_fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.ToDoViewModelFactory
import com.chari.ic.todoapp.data.database.DatabaseResult
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.databinding.FragmentTasksBinding
import com.chari.ic.todoapp.repository.ToDoRepository
import com.chari.ic.todoapp.utils.PriorityUtils
import com.chari.ic.todoapp.utils.idling_resource.EspressoIdlingResource
import java.util.*

class TasksFragment : Fragment(), SearchView.OnQueryTextListener
    , ViewTreeObserver.OnGlobalLayoutListener
{
    private val toDoViewModel by viewModels<ToDoViewModel> {
        ToDoViewModelFactory(
            ToDoRepository.getRepository()
        )
    }

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private object TODO_TASKS_DIFF_UTIL : DiffUtil.ItemCallback<ToDoTask>() {
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
        )
    }

    private var searchMenu: MenuItem? = null
    private var recyclerViewLayoutChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = toDoViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment_to_addFragment)
        }
    }

    private fun setupAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        addDragAndSwipeToDeleteFunction()

        toDoViewModel.databaseStatus.observe(viewLifecycleOwner) { status ->
            if (status is DatabaseResult.Success) {
                adapter.submitList(status.data)
            }
        }
        recyclerViewLayoutChanged = true
        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    private fun addDragAndSwipeToDeleteFunction() {
        val itemTouchHelper = ItemTouchHelper(adapter.dragAndSwipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)

        searchMenu = menu.findItem(R.id.menu_search)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
        val searchView = searchMenu?.actionView as? SearchView
        searchView?.apply {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(this@TasksFragment)
            queryHint = context.getString(R.string.search_task)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoveAll()
            R.id.menu_priority_high -> sortTasksByHighPriority()
            R.id.menu_priority_low -> sortTasksByLowPriority()
            R.id.reset_sort -> resetTasksList()
        }

        return super.onOptionsItemSelected(item)
    }

    // show AlertDialog to confirm all cachedTasks deletion
    private fun confirmRemoveAll() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                toDoViewModel.deleteAll()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.all_tasks_deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setTitle(String.format(getString(R.string.delete_tasks), adapter.itemCount))
            .setMessage(getString(R.string.sure_to_delete_all_tasks))
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
        adapter.clearContextualActionMode()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrBlank()) {
            searchDatabase(query)
        }
        collapseSearchView()

        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query.isNullOrBlank()) {
            resetTasksList()
        } else {
            searchDatabase(query)
        }

        return true
    }

    private fun resetTasksList() {
        adapter.submitList(toDoViewModel.getAllTasks.value)
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        if (!toDoViewModel.searchDatabase(searchQuery).hasObservers()) {
            toDoViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner) {
                    resultList ->
                if (adapter.currentList.toList() != resultList) {
                    recyclerViewLayoutChanged = true
                    EspressoIdlingResource.setIdleState(false)
                    adapter.submitList(resultList)
//                    { EspressoIdlingResource.setIdleState(true) }
                } else {
                    recyclerViewLayoutChanged = false
                }
                binding.recyclerView.smoothScrollToPosition(0)
            }
        }
    }

    private fun collapseSearchView() {
        searchMenu?.collapseActionView()
    }

    /**
     * Takes adapter current list and perform sorting from high to low priority compared by priority position
     * in the spinner (zero - for high, 2 - for low)
     */
    private fun sortTasksByHighPriority() {
        val sortedList = adapter.currentList.toList()
            .sortedWith { first, second ->
            PriorityUtils.getPositionByPriority(first.priority) -
                    PriorityUtils.getPositionByPriority(second.priority)
            }
        adapter.submitList(sortedList)
        binding.recyclerView.smoothScrollToPosition(0)
    }


    /**
     * Takes adapter current list and perform sorting from low to high priority compared by priority position
     * in the spinner (zero - for high, 2 - for low).
     * Negated so that position of high priority compared to
     * lower priorities would return greater number and thus lower position in the list
     */
    private fun sortTasksByLowPriority() {
        val sortedList = adapter.currentList.toList()
            .sortedWith { first, second ->
                (PriorityUtils.getPositionByPriority(first.priority) -
                        PriorityUtils.getPositionByPriority(second.priority)).unaryMinus()
            }
        adapter.submitList(sortedList)
        binding.recyclerView.smoothScrollToPosition(0)
    }

    override fun onGlobalLayout() {
        if (recyclerViewLayoutChanged) {
            EspressoIdlingResource.setIdleState(true)
        }
    }
}