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

    @Test
    fun `DueDate functionality should be integrated properly`() {
        val dueDate = Date()
        val testTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH,
            dueDate = dueDate
        )
        
        // Test that task with dueDate maintains its properties
        assertEquals(dueDate, testTask.dueDate)
        assertEquals("Test Task", testTask.title)
        assertEquals(Priority.HIGH, testTask.priority)
        
        // Test copying task with different dueDate
        val newDueDate = Date(dueDate.time + 86400000) // +1 day
        val updatedTask = testTask.copy(dueDate = newDueDate)
        assertEquals(newDueDate, updatedTask.dueDate)
        assertEquals(testTask.title, updatedTask.title)
        assertEquals(testTask.priority, updatedTask.priority)
    }

    @Test
    fun `Task should handle null dueDate properly`() {
        val testTask = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.NORMAL,
            dueDate = null
        )
        
        // Test that task with null dueDate maintains its properties
        assertNull(testTask.dueDate)
        assertEquals("Test Task", testTask.title)
        assertEquals(Priority.NORMAL, testTask.priority)
        
        // Test copying task to add dueDate
        val dueDate = Date()
        val taskWithDueDate = testTask.copy(dueDate = dueDate)
        assertEquals(dueDate, taskWithDueDate.dueDate)
        assertEquals(testTask.title, taskWithDueDate.title)
        assertEquals(testTask.priority, taskWithDueDate.priority)
    }

    @Test
    fun `DueDate callback simulation should work correctly`() {
        var capturedDueDate: Date? = null
        val onDueDateChange: (Date?) -> Unit = { newDueDate ->
            capturedDueDate = newDueDate
        }
        
        // Simulate setting a due date
        val dueDate = Date()
        onDueDateChange(dueDate)
        assertEquals(dueDate, capturedDueDate)
        
        // Simulate clearing due date
        onDueDateChange(null)
        assertNull(capturedDueDate)
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

    @Test
    fun `DueDate comparison logic should work correctly`() {
        val now = Date()
        val pastDate = Date(now.time - 86400000) // -1 day
        val futureDate = Date(now.time + 86400000) // +1 day
        val nearFutureDate = Date(now.time + 172800000) // +2 days
        val farFutureDate = Date(now.time + 432000000) // +5 days
        
        // Test past date (overdue)
        assertTrue("Past date should be before now", pastDate.before(now))
        
        // Test future dates
        assertFalse("Future date should not be before now", futureDate.before(now))
        assertFalse("Near future date should not be before now", nearFutureDate.before(now))
        assertFalse("Far future date should not be before now", farFutureDate.before(now))
        
        // Test 3-day threshold logic
        val threeDaysLater = Date(now.time + 259200000) // +3 days
        assertTrue("2-day future should be before 3-day threshold", nearFutureDate.before(threeDaysLater))
        assertFalse("5-day future should not be before 3-day threshold", farFutureDate.before(threeDaysLater))
    }
}