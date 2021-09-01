package com.chari.ic.todoapp.fragments.tasks_fragment

import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.databinding.TaskRowLayoutBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

class ToDoTaskAdapter(
    private val activity: FragmentActivity,
    private val toDoViewModel: ToDoViewModel,
    diffUtil: DiffUtil.ItemCallback<ToDoTask>
):
    ListAdapter<ToDoTask, ToDoTaskAdapter.ToDoViewHolder>(diffUtil),
    ActionMode.Callback
{
    private lateinit var actionMode: ActionMode
    private var inMultiSelectionMode = false
    private val selectedTasks = mutableMapOf<ToDoTask, ToDoViewHolder>()
    private lateinit var currentTaskView: View

    inner class ToDoViewHolder(private val binding: TaskRowLayoutBinding):
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener,
        View.OnLongClickListener {
        private var currentTask: ToDoTask? = null

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        /**  Single click */
        override fun onClick(v: View?) {
            currentTask?.let {
                if (inMultiSelectionMode) {
                    selectTask(this, currentTask!!)
                } else {
                    val action =
                        TasksFragmentDirections.actionTasksFragmentToUpdateFragment(currentTask!!)
                    binding.root.findNavController().navigate(action)
                }
            }
        }

        /**  Long click */
        override fun onLongClick(v: View?): Boolean {
            if (!inMultiSelectionMode) {
                inMultiSelectionMode = true
                activity.startActionMode(this@ToDoTaskAdapter)
            }
            currentTask?.let { selectTask(this, it) }

            return true
        }

        fun bind(toDoTask: ToDoTask) {
            binding.task = toDoTask
            currentTaskView = binding.root
            currentTask = toDoTask
            checkSelectionStyle(
                this,
                selectedTasks.containsKey(toDoTask)
            )
        }

        fun setBackgroundColor(taskRowBackgroundColor: Int, taskCardStrokeColor: Int) {
            binding.taskRowBackground.setBackgroundColor(taskRowBackgroundColor)
            binding.taskCard.strokeColor = taskCardStrokeColor
        }
    }

    private fun selectTask(
        holder: ToDoTaskAdapter.ToDoViewHolder,
        task: ToDoTask
    ) {
        val needSelection: Boolean
        if (selectedTasks.containsKey(task)) {
            needSelection = false
            selectedTasks.remove(task)
        } else {
            needSelection = true
            selectedTasks[task] = holder
        }

        checkSelectionStyle(holder, needSelection)
        checkSelectedTasksSize()
    }

    private fun checkSelectionStyle(
        holder: ToDoTaskAdapter.ToDoViewHolder,
        needSelection: Boolean
    ) {
        val backgroundColor = if (needSelection) R.color.cardBackgroundLightColor else R.color.cardBackgroundColor
        val strokeColor = if (needSelection) R.color.colorPrimary else R.color.strokeColor

        holder.setBackgroundColor(
            ContextCompat.getColor(activity, backgroundColor),
            ContextCompat.getColor(activity, strokeColor)
        )
    }

    private fun checkSelectedTasksSize() =
        when (selectedTasks.size) {
            0 -> actionMode.finish()
            else -> actionMode.title = String.format(
                    activity.getString(R.string.items_selected),
                    selectedTasks.size
                )
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TaskRowLayoutBinding.inflate(inflater, parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemByPosition(position: Int): ToDoTask = getItem(position)

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            it.menuInflater?.inflate(R.menu.tasks_action_mode_menu, menu)
            actionMode = mode
        }
        applyStatusBarColor(R.color.actionModeStatusBarColor)

        return true
    }

    private fun applyStatusBarColor(statusBarColor: Int) {
        activity.window.statusBarColor = ContextCompat.getColor(activity, statusBarColor)
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_task) {
            selectedTasks.keys.forEach {
                task -> toDoViewModel.deleteTask(task)
            }
            showSnackBar(String.format(activity.getString(R.string.items_deleted), selectedTasks.size))
            mode?.finish()
        }

        return true
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(currentTaskView, message, Snackbar.LENGTH_SHORT)
            .setAction("OK"){}
            .show()
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        inMultiSelectionMode = false
        selectedTasks.values.forEach {
            checkSelectionStyle(
                it,
                needSelection = false
            )
        }
        selectedTasks.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    fun clearContextualActionMode() {
        if (this::actionMode.isInitialized) {
            actionMode.finish()
        }
    }

    val dragAndSwipeToDeleteCallback = object: ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.END
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            clearContextualActionMode()

            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition
            val itemList = this@ToDoTaskAdapter.currentList.toMutableList()
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    val fromTask = itemList.get(i)
                    val toTask = itemList.get(Integer.min(i + 1, itemList.size - 1))
                    swapTasksInDatabase(fromTask, toTask)
                    Collections.swap(
                        itemList,
                        i,
                        i + 1
                    )
                    this@ToDoTaskAdapter.submitList(itemList)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    val fromTask = itemList.get(i)
                    val toTask = itemList.get(Math.max(i - 1, 0))
                    swapTasksInDatabase(fromTask, toTask)
                    Collections.swap(
                        itemList,
                        i,
                        i - 1
                    )
                    this@ToDoTaskAdapter.submitList(itemList)
                }
            }
            for (task in itemList) {
                toDoViewModel.updateTask(task)
            }

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            val taskToDelete = this@ToDoTaskAdapter.getItemByPosition(position)
            toDoViewModel.deleteTask(taskToDelete)
            Toast.makeText(
                currentTaskView.context,
                String.format(
                    currentTaskView.context
                        .getString(R.string.successfully_deleted_task, taskToDelete.title)
                ),
                Toast.LENGTH_SHORT
            ).show()
            tryToRestoreDeletedTask(taskToDelete)
        }
    }

    private fun swapTasksInDatabase(fromTask: ToDoTask?, toTask: ToDoTask?) {
        if (fromTask != null && toTask != null) {
            val fromTaskId = fromTask.id
            val toTaskId = toTask.id
            fromTask.id = toTaskId
            toTask.id = fromTaskId
        }
    }

    private fun tryToRestoreDeletedTask(deletedTask: ToDoTask) {
        val context = currentTaskView.context
        Snackbar.make(
            currentTaskView,
            String.format(context.getString(R.string.deleted_task), deletedTask.title),
            Snackbar.LENGTH_LONG
        )
            .setAction(context.getString(R.string.undo)) {
                toDoViewModel.insertTask(deletedTask)
            }
            .show()
    }

}