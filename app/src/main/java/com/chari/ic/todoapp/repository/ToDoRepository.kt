package com.chari.ic.todoapp.repository

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.chari.ic.todoapp.data.database.dao.ToDoDao
import com.chari.ic.todoapp.data.database.entities.ToDoTask
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToDoRepository @Inject constructor(
    private val toDoDao: ToDoDao
) : Repository {

    override fun cachedTasks(): LiveData<List<ToDoTask>> {
        if (FirebaseAuth.getInstance().currentUser != null) {
            return toDoDao.getAllTasksByUserId(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
        } else {
            return flow<List<ToDoTask>> { emit (emptyList()) }.asLiveData()
        }
    }

    override suspend fun insertTask(toDoTask: ToDoTask) {
        toDoDao.insertTask(toDoTask)
    }

    override suspend fun updateTask(toDoTask: ToDoTask) {
        toDoDao.updateTask(toDoTask)
    }

    override suspend fun deleteTask(toDoTask: ToDoTask) {
        toDoDao.deleteTask(toDoTask)
    }

    override suspend fun deleteAll() {
        toDoDao.deleteAllByUserId(FirebaseAuth.getInstance().currentUser!!.uid)
    }

    override fun searchDatabase(searchQuery: String) = toDoDao.searchDatabase(
        searchQuery,
        FirebaseAuth.getInstance().currentUser!!.uid
    )

    @VisibleForTesting
    override suspend fun fillTasksRepo(vararg tasks: ToDoTask) {
        for (task in tasks) {
            insertTask(task)
        }
    }

    @VisibleForTesting
    override suspend fun resetRepository() =
            // Clear all data to avoid test pollution.
            deleteAll()
}