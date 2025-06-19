package com.example.todo.data.database

import com.example.todo.data.model.Priority
import org.junit.Test
import org.junit.Assert.*
import java.util.Date

class TaskDatabaseTest {

    @Test
    fun `TaskEntity should have correct default priority`() {
        val taskEntity = TaskEntity(
            title = "Test Task",
            isDone = false,
            createdAt = Date()
        )
        assertEquals(Priority.NORMAL, taskEntity.priority)
    }

    @Test
    fun `TaskEntity should preserve custom priority`() {
        val taskEntity = TaskEntity(
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        assertEquals(Priority.HIGH, taskEntity.priority)
    }

    @Test
    fun `Converters should handle Priority conversion correctly`() {
        val converters = Converters()
        
        // Test all priority values
        Priority.values().forEach { priority ->
            val stringValue = converters.fromPriority(priority)
            val convertedBack = converters.toPriority(stringValue)
            assertEquals(priority, convertedBack)
        }
    }

    @Test
    fun `TaskEntity to Task conversion should preserve priority`() {
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.LOW
        )
        
        val task = taskEntity.toTask()
        assertEquals(Priority.LOW, task.priority)
    }

    @Test
    fun `Task to TaskEntity conversion should preserve priority`() {
        val task = com.example.todo.data.model.Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        
        val taskEntity = task.toTaskEntity()
        assertEquals(Priority.HIGH, taskEntity.priority)
    }
}