package com.example.todo.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.todo.data.database.TaskDao
import com.example.todo.data.database.toTask
import com.example.todo.data.database.toTaskEntity
import com.example.todo.data.model.Task

class TaskRepository(private val taskDao: TaskDao) {
    
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks().map { taskEntities ->
        taskEntities.map { it.toTask() }
    }

    suspend fun insert(task: Task) {
        taskDao.insertTask(task.toTaskEntity())
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task.toTaskEntity())
    }

    suspend fun delete(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)?.toTask()
    }
}