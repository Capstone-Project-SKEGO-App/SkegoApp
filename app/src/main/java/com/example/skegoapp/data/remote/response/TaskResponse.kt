package com.example.skegoapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class TaskResponseItem(

	@SerializedName("task_name")
	val taskName: String,

	@SerializedName("difficulty_level")
	val difficultyLevel: String,

	@SerializedName("created_at")
	val createdAt: String,

	@SerializedName("id_task")
	val idTask: Int,

	@SerializedName("days_until_deadline")
	val daysUntilDeadline: Int,

	@SerializedName("duration")
	val duration: String,

	@SerializedName("priority_score")
	val priorityScore: Float,

	@SerializedName("updated_at")
	val updatedAt: Any,

	@SerializedName("user_id")
	val userId: Int,

	@SerializedName("detail")
	val detail: String,

	@SerializedName("deadline")
	val deadline: String,

	@SerializedName("category")
	val category: String,

	@SerializedName("hour_of_day")
	val hourOfDay: Int,

	@SerializedName("status")
	val status: String,

	@SerializedName("day_of_week")
	val dayOfWeek: Int
)
