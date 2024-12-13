package com.example.skegoapp.data.pref

import android.os.Parcelable
import android.webkit.WebSettings.RenderPriority
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: Int,
    var taskId: Int,
    var title: String,
    var deadline: String,
    val fullDeadline: String,
    var difficulty: String,
    var priority: Float,
    var category: String,
    var duration: String,
    var status: String = "Not Started",
    var detail: String = ""
) : Parcelable




