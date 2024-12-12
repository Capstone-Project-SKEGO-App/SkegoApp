package com.example.skegoapp.ui

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.skegoapp.data.pref.Task
import com.example.skegoapp.data.remote.response.TasksItem

fun Context.color(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

fun TasksItem.toTask(): Task {
    return Task(
        id = userId,
        title = taskName,
        difficulty = difficultyLevel,
        deadline = deadline,
        duration = duration,
        priority = priorityScore,
        status = status,
        category = category,
        detail = detail,
        fullDeadline = deadline,
        taskId = idTask
    )
}
