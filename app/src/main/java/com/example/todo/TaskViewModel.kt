package com.example.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val tasks: LiveData<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        tasks = repository.allTasks
    }
    
    fun addTask(title: String) {
        if (title.isNotBlank()) {
            viewModelScope.launch {
                val newTask = Task(
                    id = 0, // Room will auto-generate the ID
                    title = title.trim(),
                    isDone = false
                )
                repository.insert(newTask)
            }
        }
    }
    
    fun toggleTaskDone(taskId: Int) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            task?.let {
                val updatedTask = it.copy(isDone = !it.isDone)
                repository.update(updatedTask)
            }
        }
    }
    
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.delete(taskId)
        }
    }
}