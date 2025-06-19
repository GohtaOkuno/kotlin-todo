package com.example.todo.data.model

import java.util.Date

data class Task(
    val id: Int,
    val title: String,
    val isDone: Boolean,
    val createdAt: Date,
    val priority: Priority = Priority.NORMAL
)