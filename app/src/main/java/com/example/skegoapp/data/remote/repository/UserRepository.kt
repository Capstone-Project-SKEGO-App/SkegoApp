package com.example.skegoapp.data.remote.repository

import com.example.skegoapp.data.pref.UserModel
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiConfig: ApiConfig
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiConfig: ApiConfig
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiConfig)
            }.also { instance = it }
    }
}

