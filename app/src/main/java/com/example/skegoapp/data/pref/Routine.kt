package com.example.skegoapp.data.pref

import com.google.gson.annotations.SerializedName

data class Routine(
    @SerializedName("routine_id") val routineId: Int,
    @SerializedName("routine_name") val title: String,
    @SerializedName("date_routine") val date: String,
    @SerializedName("time_routine") val time: String,
    val location: String,
    @SerializedName("decs_routine") val detail: String,
    @SerializedName("type_routine") val category: String,
    @SerializedName("user_id") val userId: Int
) :java.io.Serializable
