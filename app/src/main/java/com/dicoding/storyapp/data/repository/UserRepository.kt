package com.dicoding.storyapp.data.repository

import android.util.Log
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.request.RegisterRequest
import com.dicoding.storyapp.data.remote.response.GeneralResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException

class UserRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService

) {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(loginRequest)
            userPreference.updateUserSession(
                UserModel(
                    response.loginResult?.name,
                    response.loginResult?.userId,
                    response.loginResult?.token,
                    true
                )
            )
            Result.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
            Result.Error(errorBody.message!!)
        } catch (e: Exception) {
            Log.e(TAG, "login: ${e}")
            Result.Error(e.toString())
        }
    }

    suspend fun register(registerRequest: RegisterRequest): Result<GeneralResponse> {
        return try {
            val response = apiService.register(registerRequest)
            Log.e(TAG, "register: ${registerRequest}")
            Result.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, GeneralResponse::class.java)
            Result.Error(errorBody.message!!)
        } catch (e: Exception) {
            Log.e(TAG, "register: ${e}")
            Result.Error(e.toString())
        }
    }

    companion object {
        private const val TAG = "User Repository"

        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(userPreference, apiService).also { INSTANCE = it }
            }
    }
}