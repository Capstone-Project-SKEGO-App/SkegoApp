package com.example.skegoapp.data.remote.repository

import com.example.skegoapp.data.pref.ForgotPasswordRequest
import com.example.skegoapp.data.remote.response.ForgotPasswordResponse
import com.example.skegoapp.data.remote.retrofit.ApiService
import retrofit2.Response

class ForgotPasswordRepository(private val apiService: ApiService) {

    suspend fun changePassword(request: ForgotPasswordRequest): Response<ForgotPasswordResponse> {
        return apiService.changePassword(request)
    }
}