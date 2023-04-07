package com.chalova.irina.todoapp.tasks.ui.tasks

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.config.AppConfig
import com.chalova.irina.todoapp.config.AppConfig.ACTION_MODE_SELECTION
import com.chalova.irina.todoapp.databinding.FragmentTasksBinding
import com.chalova.irina.todoapp.login.ui.AuthEvent
import com.chalova.irina.todoapp.login.ui.AuthViewModel
import com.chalova.irina.todoapp.login.ui.LoginStatus
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.data.util.DatabaseResult
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs.LOGIN_SUCCESSFUL
import com.chalova.irina.todoapp.tasks.utils.TaskOrder
import com.chalova.irina.todoapp.utils.shortToast
import com.chalova.irina.todoapp.utils.showLongSnackBarWithAction
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksFragment: Fragment(), SearchView.OnQueryTextListener,
    ActionMode.Callback
{

    @Inject
    lateinit var viewModelFactory: TasksViewModel.TasksViewModelFactory
    private val tasksViewModel: TasksViewModel by viewModels {
        provideTasksFactory(viewModelFactory,
            findNavController().currentBackStackEntry!!.savedStateHandle)
    }

    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var binding: FragmentTasksBinding

    private object TODO_TASKS_DIFF_UTIL : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.userId == newItem.userId && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.description == newItem.description &&
                    oldItem.title == newItem.title &&
                    oldItem.priority == newItem.priority &&
                    oldItem.dueDate == newItem.dueDate
        }
    }

    private val adapter: ToDoTaskAdapter by lazy {
        ToDoTaskAdapter(requireActivity(), TODO_TASKS_DIFF_UTIL) {
                items: List<Task>, changed: Task ->
            val task = items.first { it.id == changed.id }

            val action =
                TasksFragmentDirections.actionTasksFragmentToAddEditFragment(
                    task.id, authViewModel.authState.value.userId!!
                )

            findNavController().navigate(action)
        }
    }

    private var tracker: SelectionTracker<Long>? = null
    private var actionMode: ActionMode? = null

    private val menuHost: MenuHost get() = requireActivity()
    private var searchMenu: MenuItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity)
            .activityComponent
            .tasksFragmentComponentFactory().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_tasks, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = tasksViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.loginStatus.collect { loginStatus ->
                        when (loginStatus) {
                            is LoginStatus.LoggedIn -> {
                                findNavController().currentBackStackEntry?.savedStateHandle?.let {

                                    if (it.get<Boolean>(LOGIN_SUCCESSFUL) == null) {
                                        findNavController().currentBackStackEntry!!.savedStateHandle[LOGIN_SUCCESSFUL] =
                                            false
                                        authViewModel.onAuthEvent(AuthEvent.LastLoginUnknown)
                                    }
                                }
                            }
                            is LoginStatus.LoggedOut -> {
                                findNavController().navigate(R.id.loginFragment)
                            }
                            else -> {}
                        }
                    }
                }
                launch {
                    authViewModel.authState.collect { authState ->
                        authState.userMessage?.let {
                            shortToast(it)
                            authViewModel.onUserMessageShown()
                        }
                        findNavController().currentBackStackEntry?.savedStateHandle?.set(
                            AppConfig.USER_ID, authState.userId
                        )
                    }
                }
            }
        }

        setupMenu()

        setupAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                tasksViewModel.tasksState.collect { state ->
                    if (state.tasksResult is DatabaseResult.Success) {
                        adapter.submitList(state.tasksResult.data)
                        binding.recyclerView.smoothScrollToPosition(0)
                    } else if (state.tasksResult is DatabaseResult.Empty) {
                        adapter.submitList(emptyList())
                    }

                    state.userMessage?.let {
                        shortToast(it)
                        tasksViewModel.onUserMessageShown()
                    }
                }
            }
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(
                TasksFragmentDirections.actionTasksFragmentToAddEditFragment(
                    -1, authViewModel.authState.value.userId!!)
            )
        }

    }

    private fun setupMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tasks_fragment_menu, menu)

                searchMenu = menu.findItem(R.id.menu_search)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or
                            MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                val searchView = searchMenu?.actionView as? SearchView
                searchView?.apply {
                    isSubmitButtonEnabled = true
                    setOnQueryTextListener(this@TasksFragment)
                    queryHint = getString(R.string.search_task)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_delete_all -> confirmRemoveAll()
                    R.id.high_priority_sort -> tasksViewModel.onEvent(
                        TasksEvent.OnOrderChanged(TaskOrder.Priority(TaskOrder.OrderType.Descending)))
                    R.id.old_date_sort -> tasksViewModel.onEvent(
                        TasksEvent.OnOrderChanged(TaskOrder.Date(TaskOrder.OrderType.Ascending)))
                    R.id.new_date_sort -> tasksViewModel.onEvent(
                        TasksEvent.OnOrderChanged(TaskOrder.Date(TaskOrder.OrderType.Descending)))
                }

                return false
            }

        }, viewLifecycleOwner)
    }

    private fun setupAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        setupSelectionTracker()

        setTouchActions()
    }

    private fun setupSelectionTracker() {
        tracker = SelectionTracker.Builder(
            ACTION_MODE_SELECTION,
            binding.recyclerView,
            ToDoTaskAdapter.TaskKeyProvider(adapter),
            ToDoTaskAdapter.TaskDetailLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()

                    if (actionMode == null) {
                        val currentActivity = activity as MainActivity
                        actionMode = currentActivity.startSupportActionMode(
                            this@TasksFragment)
                    }

                    val items = tracker!!.selection.size()
                    if (items > 0) {
                        actionMode?.title = resources.getQuantityString(R.plurals.tasks_selected,
                            items, items)
                    } else {
                        actionMode?.finish()
                        actionMode = null
                    }
                }
            })

        adapter.tracker = tracker
    }

    private fun setTouchActions() {
        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val taskToDelete = adapter.getItemByPosition(position)

                deleteSwipedTask(taskToDelete)
                tryToRestoreDeletedTask(taskToDelete)
            }
        })



        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun deleteSwipedTask(taskToDelete: Task) {
        tasksViewModel.onEvent(TasksEvent.DeleteTask(taskToDelete))
    }

    private fun tryToRestoreDeletedTask(deletedTask: Task) {
        showLongSnackBarWithAction(
            binding.root,
            String.format(getString(R.string.deleted_task), deletedTask.title),
            R.string.undo) {
            tasksViewModel.onEvent(TasksEvent.RestoreTask)
        }
    }

    private fun confirmRemoveAll() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                tasksViewModel.onEvent(TasksEvent.DeleteAll)
            }
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setTitle(String.format(getString(R.string.delete_tasks), adapter.itemCount))
            .setMessage(getString(R.string.sure_to_delete_all_tasks))
            .create()
            .show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchQuery(query)
        collapseSearchView()

        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        searchQuery(query)

        return true
    }

    private fun searchQuery(query: String?) {
        tasksViewModel.onEvent(TasksEvent.OnSearchQueryChanged(query))
    }

    private fun collapseSearchView() {
        searchMenu?.collapseActionView()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            it.menuInflater?.inflate(R.menu.tasks_action_mode_menu, menu)
            actionMode = mode
        }
        applyStatusBarColor(R.color.actionModeStatusBarColor)

        return true
    }

    private fun applyStatusBarColor(statusBarColor: Int) {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireActivity(), statusBarColor)
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_task) {
            tracker?.let {
                tasksViewModel.onEvent(TasksEvent.DeleteTasks(tracker!!.selection.toList()))
                showLongSnackBarWithAction(binding.root,
                    resources.getQuantityString(R.plurals.tasks_deleted,
                        tracker!!.selection.size(), tracker!!.selection.size()),
                    R.string.ok, null)
                mode?.finish()
                actionMode = null
            }
        }

        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        tracker?.clearSelection()
        actionMode = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        tracker?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        tracker?.onRestoreInstanceState(savedInstanceState)
        if (tracker != null && tracker!!.hasSelection()) {
            actionMode = (activity as MainActivity).startSupportActionMode(this@TasksFragment)
            actionMode?.title = resources.getQuantityString(R.plurals.tasks_selected, tracker!!.selection.size(), tracker!!.selection.size())
        }
        super.onViewStateRestored(savedInstanceState)
    }

}