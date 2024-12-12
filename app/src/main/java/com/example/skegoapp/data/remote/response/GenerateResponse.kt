package com.example.skegoapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class GenerateResponse(

	@SerializedName("message")
	val message: String,

	@SerializedName("predictions")
	val predictions: List<PredictionsItem>
)

data class PredictionsItem(

	@SerializedName("task_id")
	val taskId: Int,

	@SerializedName("priority_level")
	val priorityLevel: Any
)
