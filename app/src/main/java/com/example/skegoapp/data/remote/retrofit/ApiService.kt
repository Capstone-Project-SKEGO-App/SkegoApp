package com.example.skegoapp.data.remote.retrofit

import com.example.skegoapp.data.pref.AddTaskRequest
import com.example.skegoapp.data.pref.ForgotPasswordRequest
import com.example.skegoapp.data.pref.GenerateRequest
import com.example.skegoapp.data.pref.LoginRequest
import com.example.skegoapp.data.pref.RegisterRequest
import com.example.skegoapp.data.pref.Routine
import com.example.skegoapp.data.pref.UpdateTaskRequest
import com.example.skegoapp.data.remote.response.AddTaskResponse
import com.example.skegoapp.data.remote.response.DeleteTaskResponse
import com.example.skegoapp.data.remote.response.ForgotPasswordResponse
import com.example.skegoapp.data.remote.response.GenerateResponse
import com.example.skegoapp.data.remote.response.LoginResponse
import com.example.skegoapp.data.remote.response.RegisterResponse
import com.example.skegoapp.data.remote.response.SortedTasksResponse
import com.example.skegoapp.data.remote.response.TaskResponseItem
import com.example.skegoapp.data.remote.response.UpdateTaskResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("users/register")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    @POST("users/login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("users/change-password")
    suspend fun changePassword(@Body body: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("routines")
    fun addRoutine(@Body routine: Routine): Call<Routine>

    @DELETE("routines/{id}")
    fun deleteRoutine(@Path("id") id: Int): Call<Void>

    @GET("tasks")
    suspend fun getTasks(@Query("user_id") userId: Int): List<TaskResponseItem>

    @POST("tasks")
    suspend fun addTask(@Body request: AddTaskRequest): AddTaskResponse

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Int,
        @Body request: UpdateTaskRequest
    ): UpdateTaskResponse

    @GET("routines")
    suspend fun getRoutinesByUserId(
        @Query("user_id") userId: Int
    ): Response<List<Routine>>

    @GET("routines")
    suspend fun getRoutinesByUserIdAndDate(
        @Query("user_id") userId: Int,
        @Query("date_routine") dateRoutine: String
    ): Response<List<Routine>>

    @PUT("routines/{id}")
    fun updateRoutine(
        @Path("id") id: Int,
        @Body routine: Routine
    ): Call<Routine>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") taskId: Int): DeleteTaskResponse

    @POST("generates")
    suspend fun postGenerate(@Body request: GenerateRequest): GenerateResponse

    @GET("generates")
    suspend fun getSortedTasks(@Query("user_id") userId: Int): SortedTasksResponse
}
