package com.example.skegoapp.data.pref

import com.google.gson.annotations.SerializedName

data class GenerateRequest(

	@SerializedName("user_id")
	val userId: Int
)
