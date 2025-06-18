package com.example.todo.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.todo.data.model.Priority
import com.example.todo.data.model.Task
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }
    
    @TypeConverter
    fun toPriority(priorityString: String): Priority {
        return Priority.valueOf(priorityString)
    }
}

@Entity(tableName = "tasks")
@TypeConverters(Converters::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isDone: Boolean,
    val createdAt: Date = Date(),
    val priority: Priority = Priority.NORMAL
)

fun TaskEntity.toTask(): Task {
    return Task(
        id = this.id,
        title = this.title,
        isDone = this.isDone,
        createdAt = this.createdAt,
        priority = this.priority
    )
}

fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = this.id,
        title = this.title,
        isDone = this.isDone,
        createdAt = this.createdAt,
        priority = this.priority
    )
}