package com.example.todo.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import com.example.todo.data.repository.TaskRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.util.Date

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockRepository: TaskRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `Task should have default priority NORMAL when created`() {
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date()
        )
        assertEquals(Priority.NORMAL, task.priority)
    }

    @Test
    fun `Task should preserve priority when copied`() {
        val originalTask = Task(
            id = 1,
            title = "Original Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        
        val updatedTask = originalTask.copy(title = "Updated Task")
        assertEquals(Priority.HIGH, updatedTask.priority)
    }

    @Test
    fun `Priority enum should have correct values`() {
        assertEquals(3, Priority.values().size)
        assertEquals("高", Priority.HIGH.displayName)
        assertEquals("普通", Priority.NORMAL.displayName)
        assertEquals("低", Priority.LOW.displayName)
    }
}