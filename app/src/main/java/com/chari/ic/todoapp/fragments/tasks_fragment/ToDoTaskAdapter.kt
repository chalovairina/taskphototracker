package com.chari.ic.todoapp.fragments.tasks_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.data.database.entities.ToDoTask

class ToDoTaskAdapter(diffUtil: DiffUtil.ItemCallback<ToDoTask>): ListAdapter<ToDoTask, ToDoTaskAdapter.ToDoViewHolder>(diffUtil) {

    class ToDoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val titleTv = itemView.findViewById<TextView>(R.id.title_textView)
        private val descriptionTv = itemView.findViewById<TextView>(R.id.description_textView)
        private val priorityIndicator = itemView.findViewById<ImageView>(R.id.priority_indicator)
        private var currentTask: ToDoTask? = null

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (currentTask != null) {
                val action = TasksFragmentDirections.actionTasksFragmentToUpdateFragment(currentTask!!)
                itemView.findNavController().navigate(action)
            }
        }

        fun bind(toDoTask: ToDoTask) {
            currentTask = toDoTask
            titleTv.text = toDoTask.title
            descriptionTv.text = toDoTask.description
            val priority = toDoTask.priority
            setCardPriorityColor(priority)
        }

        private fun setCardPriorityColor(priority: Priority) {
            val color = when (priority) {
                Priority.HIGH -> R.color.red
                Priority.MEDIUM -> R.color.yellow
                Priority.LOW -> R.color.green
            }
            priorityIndicator.background.setTint(ContextCompat.getColor(itemView.context, color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_row_layout, parent, false)

        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}