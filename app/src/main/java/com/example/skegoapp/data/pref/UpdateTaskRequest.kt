package com.example.skegoapp.data.pref

data class UpdateTaskRequest(
    val user_id: Int,
    val task_name: String,
    val difficulty_level: String,
    val deadline: String,
    val priority_score: Float,
    val duration: String,
    val status: String,
    val category: String,
    val detail: String
)
