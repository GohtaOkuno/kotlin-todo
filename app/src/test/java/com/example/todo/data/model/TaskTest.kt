package com.example.todo.data.model

import org.junit.Test
import org.junit.Assert.*
import java.util.Date

class TaskTest {

    @Test
    fun `Task should have default priority NORMAL`() {
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date()
        )
        assertEquals(Priority.NORMAL, task.priority)
    }

    @Test
    fun `Task should accept custom priority`() {
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        assertEquals(Priority.HIGH, task.priority)
    }

    @Test
    fun `Task copy should preserve priority`() {
        val originalTask = Task(
            id = 1,
            title = "Original Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.LOW
        )
        
        val copiedTask = originalTask.copy(title = "Updated Task")
        assertEquals(Priority.LOW, copiedTask.priority)
        assertEquals("Updated Task", copiedTask.title)
        assertEquals(originalTask.id, copiedTask.id)
        assertEquals(originalTask.isDone, copiedTask.isDone)
        assertEquals(originalTask.createdAt, copiedTask.createdAt)
    }

    @Test
    fun `Task copy with priority change should work correctly`() {
        val originalTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.NORMAL
        )
        
        val updatedTask = originalTask.copy(priority = Priority.HIGH)
        assertEquals(Priority.HIGH, updatedTask.priority)
        assertEquals(originalTask.title, updatedTask.title)
        assertEquals(originalTask.id, updatedTask.id)
        assertEquals(originalTask.isDone, updatedTask.isDone)
        assertEquals(originalTask.createdAt, updatedTask.createdAt)
    }

    @Test
    fun `Task equality should include priority`() {
        val date = Date()
        val task1 = Task(1, "Test", false, date, Priority.HIGH)
        val task2 = Task(1, "Test", false, date, Priority.HIGH)
        val task3 = Task(1, "Test", false, date, Priority.LOW)
        
        assertEquals(task1, task2)
        assertNotEquals(task1, task3)
    }

    @Test
    fun `Task should have default dueDate as null`() {
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date()
        )
        assertNull(task.dueDate)
    }

    @Test
    fun `Task should accept custom dueDate`() {
        val dueDate = Date()
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            dueDate = dueDate
        )
        assertEquals(dueDate, task.dueDate)
    }

    @Test
    fun `Task copy should preserve dueDate`() {
        val dueDate = Date()
        val originalTask = Task(
            id = 1,
            title = "Original Task",
            isDone = false,
            createdAt = Date(),
            dueDate = dueDate
        )
        
        val copiedTask = originalTask.copy(title = "Updated Task")
        assertEquals(dueDate, copiedTask.dueDate)
        assertEquals("Updated Task", copiedTask.title)
    }

    @Test
    fun `Task copy with dueDate change should work correctly`() {
        val originalDueDate = Date()
        val newDueDate = Date(originalDueDate.time + 86400000) // +1 day
        
        val originalTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            dueDate = originalDueDate
        )
        
        val updatedTask = originalTask.copy(dueDate = newDueDate)
        assertEquals(newDueDate, updatedTask.dueDate)
        assertEquals(originalTask.title, updatedTask.title)
    }

    @Test
    fun `Task copy can set dueDate to null`() {
        val originalTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            dueDate = Date()
        )
        
        val updatedTask = originalTask.copy(dueDate = null)
        assertNull(updatedTask.dueDate)
    }

    @Test
    fun `Task equality should include dueDate`() {
        val date = Date()
        val dueDate = Date()
        val task1 = Task(1, "Test", false, date, Priority.NORMAL, dueDate)
        val task2 = Task(1, "Test", false, date, Priority.NORMAL, dueDate)
        val task3 = Task(1, "Test", false, date, Priority.NORMAL, null)
        
        assertEquals(task1, task2)
        assertNotEquals(task1, task3)
    }
}