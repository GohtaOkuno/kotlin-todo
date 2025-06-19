package com.example.todo.data.database

import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import org.junit.Test
import org.junit.Assert.*
import java.util.Date

class TaskEntityTest {

    @Test
    fun `TaskEntity should have default priority NORMAL`() {
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date()
        )
        assertEquals(Priority.NORMAL, taskEntity.priority)
    }

    @Test
    fun `TaskEntity should accept custom priority`() {
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        assertEquals(Priority.HIGH, taskEntity.priority)
    }

    @Test
    fun `TaskEntity toTask conversion should preserve all fields including priority`() {
        val date = Date()
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = true,
            createdAt = date,
            priority = Priority.LOW
        )
        
        val task = taskEntity.toTask()
        
        assertEquals(taskEntity.id, task.id)
        assertEquals(taskEntity.title, task.title)
        assertEquals(taskEntity.isDone, task.isDone)
        assertEquals(taskEntity.createdAt, task.createdAt)
        assertEquals(taskEntity.priority, task.priority)
    }

    @Test
    fun `Task toTaskEntity conversion should preserve all fields including priority`() {
        val date = Date()
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = true,
            createdAt = date,
            priority = Priority.HIGH
        )
        
        val taskEntity = task.toTaskEntity()
        
        assertEquals(task.id, taskEntity.id)
        assertEquals(task.title, taskEntity.title)
        assertEquals(task.isDone, taskEntity.isDone)
        assertEquals(task.createdAt, taskEntity.createdAt)
        assertEquals(task.priority, taskEntity.priority)
    }

    @Test
    fun `Converters fromPriority should convert Priority to String`() {
        val converters = Converters()
        
        assertEquals("HIGH", converters.fromPriority(Priority.HIGH))
        assertEquals("NORMAL", converters.fromPriority(Priority.NORMAL))
        assertEquals("LOW", converters.fromPriority(Priority.LOW))
    }

    @Test
    fun `Converters toPriority should convert String to Priority`() {
        val converters = Converters()
        
        assertEquals(Priority.HIGH, converters.toPriority("HIGH"))
        assertEquals(Priority.NORMAL, converters.toPriority("NORMAL"))
        assertEquals(Priority.LOW, converters.toPriority("LOW"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Converters toPriority should throw exception for invalid string`() {
        val converters = Converters()
        converters.toPriority("INVALID")
    }

    @Test
    fun `Round trip conversion should preserve priority`() {
        val originalTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        
        val taskEntity = originalTask.toTaskEntity()
        val convertedTask = taskEntity.toTask()
        
        assertEquals(originalTask.priority, convertedTask.priority)
        assertEquals(originalTask, convertedTask)
    }

    @Test
    fun `TaskEntity should have default dueDate as null`() {
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date()
        )
        assertNull(taskEntity.dueDate)
    }

    @Test
    fun `TaskEntity should accept custom dueDate`() {
        val dueDate = Date()
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            dueDate = dueDate
        )
        assertEquals(dueDate, taskEntity.dueDate)
    }

    @Test
    fun `TaskEntity toTask conversion should preserve dueDate`() {
        val date = Date()
        val dueDate = Date()
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = true,
            createdAt = date,
            priority = Priority.LOW,
            dueDate = dueDate
        )
        
        val task = taskEntity.toTask()
        
        assertEquals(taskEntity.id, task.id)
        assertEquals(taskEntity.title, task.title)
        assertEquals(taskEntity.isDone, task.isDone)
        assertEquals(taskEntity.createdAt, task.createdAt)
        assertEquals(taskEntity.priority, task.priority)
        assertEquals(taskEntity.dueDate, task.dueDate)
    }

    @Test
    fun `TaskEntity toTask conversion should handle null dueDate`() {
        val date = Date()
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = true,
            createdAt = date,
            priority = Priority.LOW,
            dueDate = null
        )
        
        val task = taskEntity.toTask()
        
        assertNull(task.dueDate)
        assertEquals(taskEntity.id, task.id)
        assertEquals(taskEntity.title, task.title)
    }

    @Test
    fun `Task toTaskEntity conversion should preserve dueDate`() {
        val date = Date()
        val dueDate = Date()
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = true,
            createdAt = date,
            priority = Priority.HIGH,
            dueDate = dueDate
        )
        
        val taskEntity = task.toTaskEntity()
        
        assertEquals(task.id, taskEntity.id)
        assertEquals(task.title, taskEntity.title)
        assertEquals(task.isDone, taskEntity.isDone)
        assertEquals(task.createdAt, taskEntity.createdAt)
        assertEquals(task.priority, taskEntity.priority)
        assertEquals(task.dueDate, taskEntity.dueDate)
    }

    @Test
    fun `Task toTaskEntity conversion should handle null dueDate`() {
        val date = Date()
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = true,
            createdAt = date,
            priority = Priority.HIGH,
            dueDate = null
        )
        
        val taskEntity = task.toTaskEntity()
        
        assertNull(taskEntity.dueDate)
        assertEquals(task.id, taskEntity.id)
        assertEquals(task.title, taskEntity.title)
    }

    @Test
    fun `Round trip conversion should preserve dueDate`() {
        val dueDate = Date()
        val originalTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH,
            dueDate = dueDate
        )
        
        val taskEntity = originalTask.toTaskEntity()
        val convertedTask = taskEntity.toTask()
        
        assertEquals(originalTask.dueDate, convertedTask.dueDate)
        assertEquals(originalTask, convertedTask)
    }

    @Test
    fun `Round trip conversion should preserve null dueDate`() {
        val originalTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH,
            dueDate = null
        )
        
        val taskEntity = originalTask.toTaskEntity()
        val convertedTask = taskEntity.toTask()
        
        assertNull(convertedTask.dueDate)
        assertEquals(originalTask, convertedTask)
    }
}