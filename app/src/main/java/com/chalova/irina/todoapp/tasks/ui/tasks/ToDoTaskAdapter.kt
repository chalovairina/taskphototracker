package com.chalova.irina.todoapp.tasks.ui.tasks

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.databinding.TaskRowLayoutBinding
import com.chalova.irina.todoapp.tasks.data.Task

class ToDoTaskAdapter(
    private val activity: FragmentActivity,
    diffUtil: DiffUtil.ItemCallback<Task>,
    private val action: (items: List<Task>, changed: Task) -> Unit
):
    ListAdapter<Task, ToDoTaskAdapter.ToDoViewHolder>(diffUtil)
{

    var tracker: SelectionTracker<Long>? = null

    inner class ToDoViewHolder(private val taskBinding: TaskRowLayoutBinding):
        RecyclerView.ViewHolder(taskBinding.root), View.OnClickListener {

            private var currentTask: Task? = null

        init {
            taskBinding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            action(currentList, taskBinding.task!!)
        }

        fun bind(task: Task) {
            taskBinding.task = task
            taskBinding.executePendingBindings()

            currentTask = task

            tracker?.let {
                val backgroundColor = if (it.isSelected(task.id)) R.color.cardBackgroundLightColor else
                    R.color.cardBackgroundColor
                val strokeColor = if (it.isSelected(task.id)) R.color.colorPrimary else
                    R.color.strokeColor

                setBackgroundColor(
                    ContextCompat.getColor(activity, backgroundColor),
                    ContextCompat.getColor(activity, strokeColor)
                )
            }
        }

        fun setBackgroundColor(taskRowBackgroundColor: Int, taskCardStrokeColor: Int) {
            taskBinding.taskRowBackground.setBackgroundColor(taskRowBackgroundColor)
            taskBinding.taskCard.strokeColor = taskCardStrokeColor
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = bindingAdapterPosition

                override fun getSelectionKey(): Long = currentList[bindingAdapterPosition].id
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TaskRowLayoutBinding.inflate(inflater, parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemByPosition(position: Int): Task = getItem(position)

    class TaskKeyProvider(private val adapter: ToDoTaskAdapter) : ItemKeyProvider<Long>(SCOPE_CACHED) {
        override fun getKey(position: Int) = adapter.getItem(position).id
        override fun getPosition(key: Long) = adapter.currentList.indexOfFirst { it.id == key }
    }

    class TaskDetailLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {

        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            return view?.let {
                (recyclerView.getChildViewHolder(view) as ToDoTaskAdapter.ToDoViewHolder)
                    .getItemDetails()
            } ?: run {
                return null
            }
        }
    }
}