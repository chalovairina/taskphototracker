package com.chari.ic.todoapp

import android.app.Application
import com.chari.ic.todoapp.data.database.ToDoDatabase
import com.chari.ic.todoapp.repository.ToDoRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ToDoApplication: Application() {

}