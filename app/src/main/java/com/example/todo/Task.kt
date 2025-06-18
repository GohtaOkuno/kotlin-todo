package com.example.todo

import java.util.Date

data class Task(
    val id: Int,
    val title: String,
    val isDone: Boolean,
    val createdAt: Date
)