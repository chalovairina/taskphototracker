package com.chari.ic.todoapp.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
@Entity(tableName = "todo_tasks")
data class ToDoTask(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var userId: String,
    var title: String,
    var priority: Priority,
    var description: String,
    var dueDate: Instant,
    var createdAt: Instant,
    var completed: Boolean
): Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ToDoTask

        if (userId != other.userId) return false
        if (title != other.title) return false
        if (priority != other.priority) return false
        if (dueDate != other.dueDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + priority.hashCode()
        result = 31 * result + dueDate.hashCode()
        return result
    }


}