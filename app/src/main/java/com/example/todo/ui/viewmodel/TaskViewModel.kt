package com.example.todo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todo.data.database.TaskDatabase
import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import com.example.todo.data.repository.TaskRepository
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val tasks: LiveData<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        tasks = repository.allTasks
    }
    
    fun addTask(title: String, priority: Priority = Priority.NORMAL) {
        if (title.isNotBlank()) {
            viewModelScope.launch {
                val newTask = Task(
                    id = 0, // Room will auto-generate the ID
                    title = title.trim(),
                    isDone = false,
                    createdAt = java.util.Date(),
                    priority = priority
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
    
    fun updateTask(taskId: Int, newTitle: String) {
        if (newTitle.isNotBlank()) {
            viewModelScope.launch {
                val task = repository.getTaskById(taskId)
                task?.let {
                    val updatedTask = it.copy(title = newTitle.trim())
                    repository.update(updatedTask)
                }
            }
        }
    }
    
    fun updateTaskPriority(taskId: Int, priority: Priority) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            task?.let {
                val updatedTask = it.copy(priority = priority)
                repository.update(updatedTask)
            }
        }
    }
    
    suspend fun getTaskById(taskId: Int): Task? {
        return repository.getTaskById(taskId)
    }
    
    fun updateTaskDueDate(taskId: Int, dueDate: Date?) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            task?.let {
                val updatedTask = it.copy(dueDate = dueDate)
                repository.update(updatedTask)
            }
        }
    }
}