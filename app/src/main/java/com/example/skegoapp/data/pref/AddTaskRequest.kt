package com.example.skegoapp.data.pref

data class AddTaskRequest(
    val user_id: Int,
    val task_name: String,
    val difficulty_level: String,
    val deadline: String,
    val priority_score: Int?,
    val duration: String,
    val status: String,
    val category: String,
    val detail: String
)