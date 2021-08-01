package com.chari.ic.todoapp.fragments.tasks_fragment

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.chari.ic.todoapp.utils.Constants
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

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

    inner class ToDoViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener,
        View.OnLongClickListener
    {
        private val titleTv = itemView.findViewById<TextView>(R.id.title_textView)
        private val descriptionTv = itemView.findViewById<TextView>(R.id.description_textView)
        private val priorityIndicator = itemView.findViewById<ImageView>(R.id.priority_indicator)
        var currentTask: ToDoTask? = null

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        /**  Single click */
        override fun onClick(v: View?) {
            currentTask?.let {
                if (inMultiSelectionMode) {
                    selectTask(this, currentTask!!)
                } else {
                    val action =
                        TasksFragmentDirections.actionTasksFragmentToUpdateFragment(currentTask!!)
                    itemView.findNavController().navigate(action)
                }
            }
        }

        /**  Long click */
        override fun onLongClick(v: View?): Boolean {
            return if (!inMultiSelectionMode) {
                inMultiSelectionMode = true
                activity.startActionMode(this@ToDoTaskAdapter)
                currentTask?.let { selectTask(this, it) }
                true
            } else {
                currentTask?.let { selectTask(this, it) }
                true
            }
        }

        fun bind(toDoTask: ToDoTask) {
            currentTaskView = this.itemView
            fillTask(toDoTask)
            checkSelectionStyle(
                this,
                selectedTasks.containsKey(toDoTask)
            )
        }

        private fun fillTask(toDoTask: ToDoTask) {
            currentTask = toDoTask
            titleTv.text = toDoTask.title
            descriptionTv.text = toDoTask.description
            val priority = toDoTask.priority
            setPriorityColor(priority)
        }

        private fun setPriorityColor(priority: Priority) {
            val color = when (priority) {
                Priority.HIGH -> Constants.PRIORITY_COLOR_HIGH
                Priority.MEDIUM -> Constants.PRIORITY_COLOR_MEDIUM
                Priority.LOW -> Constants.PRIORITY_COLOR_LOW
            }
            priorityIndicator.background.setTint(ContextCompat.getColor(itemView.context, color))
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

        checkSelectionStyle(
            holder,
            needSelection
        )
        checkSelectedTasksSize()
    }

    private fun checkSelectedTasksSize() {
        when (selectedTasks.size) {
            0 -> {
                actionMode.finish()
            }
            else -> {
                actionMode.title = String.format(
                    activity.getString(R.string.items_selected),
                    selectedTasks.size
                )
            }
        }
    }

    private fun checkSelectionStyle(
        holder: ToDoTaskAdapter.ToDoViewHolder,
        needSelection: Boolean
    ) {
        val backgroundColor = if (needSelection) R.color.cardBackgroundLightColor else R.color.cardBackgroundColor
        val strokeColor = if (needSelection) R.color.colorPrimary else R.color.strokeColor

        holder.itemView.findViewById<ConstraintLayout>(R.id.task_row_background)
            .setBackgroundColor(
                ContextCompat.getColor(activity, backgroundColor))
        holder.itemView.findViewById<MaterialCardView>(R.id.task_card).strokeColor =
            ContextCompat.getColor(activity, strokeColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_row_layout, parent, false)

        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bind(getItem(position))
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
}