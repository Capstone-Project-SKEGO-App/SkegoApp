package com.example.skegoapp.data.remote.repository

import com.example.skegoapp.data.pref.AddTaskRequest
import com.example.skegoapp.data.pref.GenerateRequest
import com.example.skegoapp.data.pref.UpdateTaskRequest
import com.example.skegoapp.data.remote.response.AddTaskResponse
import com.example.skegoapp.data.remote.response.DeleteTaskResponse
import com.example.skegoapp.data.remote.response.GenerateResponse
import com.example.skegoapp.data.remote.response.SortedTasksResponse
import com.example.skegoapp.data.remote.response.TaskResponseItem
import com.example.skegoapp.data.remote.response.UpdateTaskResponse
import com.example.skegoapp.data.remote.retrofit.ApiService

class TaskRepository(private val apiService: ApiService) {

    suspend fun getTasks(userId: Int): List<TaskResponseItem> {
        return apiService.getTasks(userId)
    }

    suspend fun addTask(taskRequest: AddTaskRequest): AddTaskResponse {
        return apiService.addTask(taskRequest)
    }

    suspend fun updateTask(taskId: Int, request: UpdateTaskRequest): UpdateTaskResponse {
        return apiService.updateTask(taskId, request)
    }

    suspend fun deleteTask(taskId: Int): DeleteTaskResponse {
        return apiService.deleteTask(taskId)
    }

    suspend fun postGenerate(userId: Int): GenerateResponse {
        return apiService.postGenerate(GenerateRequest(userId))
    }

    suspend fun getSortedTasks(userId: Int): SortedTasksResponse {
        return apiService.getSortedTasks(userId)
    }
}