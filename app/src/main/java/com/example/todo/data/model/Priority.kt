package com.example.todo.data.model

enum class Priority(val displayName: String, val colorRes: Int) {
    HIGH("高", android.R.color.holo_red_light),
    NORMAL("普通", android.R.color.holo_blue_light),
    LOW("低", android.R.color.holo_green_light)
}