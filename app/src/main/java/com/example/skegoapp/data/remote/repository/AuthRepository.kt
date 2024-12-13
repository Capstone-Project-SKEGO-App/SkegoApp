package com.example.skegoapp.data.remote.repository

import com.example.skegoapp.data.pref.LoginRequest
import com.example.skegoapp.data.pref.RegisterRequest
import com.example.skegoapp.data.remote.response.LoginResponse
import com.example.skegoapp.data.remote.response.RegisterResponse
import com.example.skegoapp.data.remote.retrofit.ApiService

class AuthRepository(private val apiService: ApiService) {

    suspend fun registerUser(username: String, email: String, password: String): RegisterResponse {
        val registerRequest = RegisterRequest(username, email, password)
        return apiService.registerUser(registerRequest)
    }

    suspend fun loginUser(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        return apiService.loginUser(loginRequest)
    }
}