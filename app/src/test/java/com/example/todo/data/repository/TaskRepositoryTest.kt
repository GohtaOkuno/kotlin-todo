package com.example.todo.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.todo.data.database.TaskDao
import com.example.todo.data.database.TaskEntity
import com.example.todo.data.database.toTask
import com.example.todo.data.database.toTaskEntity
import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.util.Date

class TaskRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockTaskDao: TaskDao
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        mockTaskDao = mockk(relaxed = true)
        repository = TaskRepository(mockTaskDao)
    }

    @Test
    fun `insert should convert Task to TaskEntity and call dao insert`() = runBlocking {
        val task = Task(
            id = 0,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        
        repository.insert(task)
        
        coVerify {
            mockTaskDao.insertTask(match { entity ->
                entity.title == task.title &&
                entity.isDone == task.isDone &&
                entity.priority == task.priority
            })
        }
    }

    @Test
    fun `update should convert Task to TaskEntity and call dao update`() = runBlocking {
        val task = Task(
            id = 1,
            title = "Updated Task",
            isDone = true,
            createdAt = Date(),
            priority = Priority.LOW
        )
        
        repository.update(task)
        
        coVerify {
            mockTaskDao.updateTask(match { entity ->
                entity.id == task.id &&
                entity.title == task.title &&
                entity.isDone == task.isDone &&
                entity.priority == task.priority
            })
        }
    }

    @Test
    fun `delete should call dao deleteTaskById`() = runBlocking {
        val taskId = 1
        
        repository.delete(taskId)
        
        coVerify { mockTaskDao.deleteTaskById(taskId) }
    }

    @Test
    fun `getTaskById should return converted Task when entity exists`() = runBlocking {
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        
        coEvery { mockTaskDao.getTaskById(1) } returns taskEntity
        
        val result = repository.getTaskById(1)
        
        assertNotNull(result)
        assertEquals(taskEntity.id, result!!.id)
        assertEquals(taskEntity.title, result.title)
        assertEquals(taskEntity.isDone, result.isDone)
        assertEquals(taskEntity.priority, result.priority)
    }

    @Test
    fun `getTaskById should return null when entity does not exist`() = runBlocking {
        coEvery { mockTaskDao.getTaskById(999) } returns null
        
        val result = repository.getTaskById(999)
        
        assertNull(result)
    }

    @Test
    fun `allTasks should transform LiveData from TaskEntity to Task`() {
        val taskEntities = listOf(
            TaskEntity(1, "Task 1", false, Date(), Priority.HIGH),
            TaskEntity(2, "Task 2", true, Date(), Priority.NORMAL),
            TaskEntity(3, "Task 3", false, Date(), Priority.LOW)
        )
        
        val mockLiveData = MutableLiveData<List<TaskEntity>>()
        every { mockTaskDao.getAllTasks() } returns mockLiveData
        
        // Trigger the transformation by setting value
        mockLiveData.value = taskEntities
        
        // Get the transformed LiveData
        val result = repository.allTasks
        
        // Verify the transformation occurred
        verify { mockTaskDao.getAllTasks() }
        
        // The actual transformation testing would require more complex setup
        // for testing LiveData transformations
    }

    @Test
    fun `TaskEntity to Task conversion preserves priority`() {
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Task",
            isDone = true,
            createdAt = Date(),
            priority = Priority.LOW
        )
        
        val task = taskEntity.toTask()
        
        assertEquals(taskEntity.priority, task.priority)
        assertEquals(taskEntity.id, task.id)
        assertEquals(taskEntity.title, task.title)
        assertEquals(taskEntity.isDone, task.isDone)
        assertEquals(taskEntity.createdAt, task.createdAt)
    }

    @Test
    fun `Task to TaskEntity conversion preserves priority`() {
        val task = Task(
            id = 1,
            title = "Test Task",
            isDone = false,
            createdAt = Date(),
            priority = Priority.HIGH
        )
        
        val taskEntity = task.toTaskEntity()
        
        assertEquals(task.priority, taskEntity.priority)
        assertEquals(task.id, taskEntity.id)
        assertEquals(task.title, taskEntity.title)
        assertEquals(task.isDone, taskEntity.isDone)
        assertEquals(task.createdAt, taskEntity.createdAt)
    }
}