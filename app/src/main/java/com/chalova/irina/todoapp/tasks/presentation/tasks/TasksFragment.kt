package com.chalova.irina.todoapp.tasks.presentation.tasks

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.config.AppConfig.ACTION_MODE_SELECTION
import com.chalova.irina.todoapp.databinding.FragmentTasksBinding
import com.chalova.irina.todoapp.di.provideTasksFactory
import com.chalova.irina.todoapp.tasks.data.Task
import com.chalova.irina.todoapp.tasks.presentation.utils.SelectionTrackerProvider
import com.chalova.irina.todoapp.tasks.utils.TaskOrder
import com.chalova.irina.todoapp.utils.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject

class TasksFragment : Fragment(), SearchView.OnQueryTextListener {

    @Inject
    lateinit var viewModelFactory: TasksViewModel.TasksViewModelFactory
    private val tasksViewModel: TasksViewModel by viewModels {
        provideTasksFactory(
            viewModelFactory, findNavController().currentBackStackEntry!!.savedStateHandle
        )
    }

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val menuHost: MenuHost get() = requireActivity()
    private var searchMenu: MenuItem? = null

    private val actionModeSelectionTracker: ActionModeSelectionTracker by lazy {
        ActionModeSelectionTracker()
    }

    private val tasksAdapter: TasksAdapter by lazy {
        TasksAdapter(
            requireActivity(), { items: List<Task>, changed: Task ->
                val task = items.first { it.id == changed.id }
                findNavController().navigate(
                    TasksFragmentDirections
                        .actionTasksFragmentToAddEditFragment(task.id)
                )
            }, { task: Task ->
                tasksViewModel.onEvent(TasksEvent.UpdateTask(task))
            }, { completing: Task ->
                tasksViewModel.onEvent(TasksEvent.CompletingTask(completing))
                takePhoto.launch()
            }
        )
    }

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
            bmp?.let {

                val fileName = UUID.randomUUID().toString()
                Timber.d("takePhoto $it $fileName")
                val result = if (Build.VERSION.SDK_INT >= Q)
                    savePhotoToInternalStorage(fileName, it)
                else savePhotoToExternalStorage(fileName, it)
                Timber.d("savePhoto")
                when (result) {
                    is Result.Success -> {
                        tasksViewModel.onEvent(TasksEvent.CompletePhotoReport(fileName))
                        shortToast(R.string.photo_report_saved_successfully)
                    }
                    is Result.Error -> shortToast(R.string.photo_report_save_failed)
                }
            }
        }

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
        _binding = FragmentTasksBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupAdapter()
        setupUiState()
    }

    private fun setupMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_tasks, menu)

                searchMenu = menu.findItem(R.id.menu_search)
                    .setShowAsActionFlags(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM or
                                MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                    )
                val searchView = searchMenu?.actionView as? SearchView
                searchView?.apply {
                    isSubmitButtonEnabled = true
                    setOnQueryTextListener(this@TasksFragment)
                    queryHint = getString(R.string.tasks_search_task)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_delete_all -> confirmRemoveAll()
                    R.id.high_priority_sort -> tasksViewModel.onEvent(
                        TasksEvent.OrderChanged(TaskOrder.Priority(TaskOrder.OrderType.Descending))
                    )
                    R.id.old_date_sort -> tasksViewModel.onEvent(
                        TasksEvent.OrderChanged(TaskOrder.Date(TaskOrder.OrderType.Ascending))
                    )
                    R.id.new_date_sort -> tasksViewModel.onEvent(
                        TasksEvent.OrderChanged(TaskOrder.Date(TaskOrder.OrderType.Descending))
                    )
                }

                return false
            }

        }, viewLifecycleOwner)
    }

    private fun setupUiState() {
        viewLifecycleOwner.repeatOnState(Lifecycle.State.STARTED) {
            launch {
                tasksViewModel.userMessage.collect { message ->
                    shortToast(message)
                }
            }
            launch {
                tasksViewModel.tasksState.collect { state ->
                    setupUiElements(state)
                    tasksAdapter.submitList(state.tasksList)
                }
            }
            launch {
                tasksViewModel.userMessage.collect { userMessage ->
                    shortToast(userMessage)
                }
            }
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(
                TasksFragmentDirections.actionTasksFragmentToAddEditFragment(-1)
            )
        }
    }

    private fun setupUiElements(state: TasksState) {
        setupShimmeringFX(state)
        setupUiElementsVisibility(state)
    }

    private fun setupShimmeringFX(state: TasksState) {
        binding.shimmerFrameLayout.apply {
            if (state.isLoading) {
                visibility = View.VISIBLE
                startShimmer()
            } else {
                stopShimmer()
                visibility = View.INVISIBLE
            }
        }
    }

    private fun setupUiElementsVisibility(state: TasksState) {
        with(binding) {
            rvTasks.isVisible = state.tasksList.isNotEmpty()
            noDataImageView.isVisible = state.tasksList.isEmpty()
            noDataTextView.isVisible = state.tasksList.isEmpty()
        }
    }

    private fun setupAdapter() {
        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tasksAdapter
        }

        setupSelectionTracker()
        setTouchActions()
    }

    private fun setupSelectionTracker() {
        tasksAdapter.tracker = actionModeSelectionTracker.provideSelectionTracker()
    }

    private fun setTouchActions() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
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
                val taskToDelete = tasksAdapter.getItemByPosition(position)

                deleteSwipedTask(taskToDelete)
                tryToRestoreDeletedTask(taskToDelete)
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.rvTasks)
    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Result<Nothing> {
        Timber.d("savePhotoToInternalStorage")
        return try {
            requireActivity().openFileOutput("$filename.jpg", AppCompatActivity.MODE_PRIVATE)
                .use { stream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            Result.Success()
        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(ErrorResult.UnknownError())
        }
    }

    private fun savePhotoToExternalStorage(displayName: String, bitmap: Bitmap): Result<Nothing> {
        val imageCollection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/.jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        return try {
            requireActivity().contentResolver.insert(imageCollection, contentValues)?.let { uri ->
                requireActivity().contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            Result.Success()
        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(ErrorResult.UnknownError())
        }
    }

    private fun deleteSwipedTask(taskToDelete: Task) {
        tasksViewModel.onEvent(TasksEvent.DeleteTask(taskToDelete))
    }

    private fun tryToRestoreDeletedTask(deletedTask: Task) {
        showLongSnackBarWithAction(
            binding.root,
            String.format(getString(R.string.tasks_deleted), deletedTask.title), R.string.tasks_undo
        ) {
            tasksViewModel.onEvent(TasksEvent.RestoreTask)
        }
    }

    private fun confirmRemoveAll() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(getString(R.string.tasks_yes)) { _, _ ->
                tasksViewModel.onEvent(TasksEvent.DeleteAll)
            }
            .setNegativeButton(getString(R.string.tasks_no)) { _, _ -> }
            .setTitle(String.format(getString(R.string.tasks_delete), tasksAdapter.itemCount))
            .setMessage(getString(R.string.tasks_confirm_delete_all))
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
        tasksViewModel.onEvent(TasksEvent.SearchQueryChanged(query))
    }

    private fun collapseSearchView() {
        searchMenu?.collapseActionView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        actionModeSelectionTracker.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        actionModeSelectionTracker.onRestoreSavedState(savedInstanceState)
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroyView() {
        binding.rvTasks.adapter = null
        super.onDestroyView()
        _binding = null
    }

    inner class ActionModeSelectionTracker :
        ActionMode.Callback, SelectionTrackerProvider<Long> {

        private lateinit var tracker: SelectionTracker<Long>
        private var actionMode: ActionMode? = null

        init {
            setupSelectionTracker()
        }

        override fun provideSelectionTracker(): SelectionTracker<Long> {
            return tracker
        }

        private fun setupSelectionTracker() {
            tracker = SelectionTracker.Builder(
                ACTION_MODE_SELECTION,
                binding.rvTasks,
                TasksAdapter.TaskKeyProvider(binding.rvTasks.adapter as TasksAdapter),
                TasksAdapter.TaskDetailLookup(binding.rvTasks),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()

            addSelectionObserver()
        }

        private fun addSelectionObserver() {
            tracker.addObserver(
                object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()
                        if (actionMode == null) {
                            actionMode = (activity as MainActivity).startSupportActionMode(
                                this@ActionModeSelectionTracker
                            )
                            applyStatusBarColor(R.attr.colorActionModeStatusBar)
                        }
                        val items = tracker.selection.size()
                        if (items > 0) {
                            actionMode?.title = binding.root.context.resources.getQuantityString(
                                R.plurals.tasks_selected,
                                items, items
                            )
                        } else {
                            actionMode?.finish()
                            actionMode = null
                        }
                    }
                })
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.let {
                it.menuInflater?.inflate(R.menu.action_mode, menu)
                actionMode = mode
            }

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            if (item?.itemId == R.id.delete_task) {
                tracker.let {
                    tasksViewModel.onEvent(TasksEvent.DeleteTasks(tracker.selection.toList()))
                    showLongSnackBarWithAction(
                        binding.root,
                        resources.getQuantityString(
                            R.plurals.tasks_deleted,
                            tracker.selection.size(), tracker.selection.size()
                        ),
                        R.string.tasks_ok, null
                    )
                    mode?.finish()
                    actionMode = null
                    applyStatusBarColor(android.R.attr.statusBarColor)
                }
            }

            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            applyStatusBarColor(android.R.attr.statusBarColor)
            tracker.clearSelection()
            actionMode = null
        }

        fun onSaveInstanceState(outState: Bundle) {
            tracker.onSaveInstanceState(outState)
        }

        fun onRestoreSavedState(savedInstanceState: Bundle?) {
            tracker.onRestoreInstanceState(savedInstanceState)
            if (tracker.hasSelection()) {
                actionMode = (activity as MainActivity).startSupportActionMode(this)
                applyStatusBarColor(R.attr.colorActionModeStatusBar)
                actionMode?.title = resources.getQuantityString(
                    R.plurals.tasks_selected,
                    tracker.selection.size(),
                    tracker.selection.size()
                )
            }
        }

        private fun applyStatusBarColor(statusBarColor: Int) {
            activity?.window?.statusBarColor = requireActivity().getColorFromAttr(statusBarColor)
        }
    }
}