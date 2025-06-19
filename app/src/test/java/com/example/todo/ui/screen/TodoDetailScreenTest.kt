package com.example.todo.ui.screen

import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import org.junit.Test
import org.junit.Assert.*
import java.util.Date

class TodoDetailScreenTest {

    @Test
    fun `Priority functionality should be integrated properly`() {
        val testTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        
        // Test that task with HIGH priority maintains its properties
        assertEquals(Priority.HIGH, testTask.priority)
        assertEquals("Test Task", testTask.title)
        assertEquals(false, testTask.isDone)
        
        // Test priority display name
        assertEquals("高", testTask.priority.displayName)
        
        // Test copying task with different priority
        val lowPriorityTask = testTask.copy(priority = Priority.LOW)
        assertEquals(Priority.LOW, lowPriorityTask.priority)
        assertEquals("低", lowPriorityTask.priority.displayName)
    }
}