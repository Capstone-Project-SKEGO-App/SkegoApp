package com.example.skegoapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddTaskResponse(

	@SerializedName("message")
	val message: String,

	@SerializedName("taskId")
	val taskId: Int
)
