package com.dicoding.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.data.remote.response.GeneralResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.Story
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository(
    private val apiService: ApiService
) {

    fun getStories(location: String): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(location)
            val stories = response.listStory.orEmpty().filterNotNull()
            emit(Result.Success(stories))
        } catch (e: Exception) {
            Log.d(TAG, "getStories: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailStoryById(id: String): LiveData<Result<Story?>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStoryById(id)
            val story = response.story
            emit(Result.Success(story))
        } catch (e: Exception) {
            Log.d(TAG, "getDetailStoryById: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun addStory(image: File, description: String): Result<GeneralResponse> {
        Result.Loading
        return try {
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImage = image.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                image.name,
                requestImage
            )
            val response = apiService.addStory(multipartBody, requestBody)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }
    
    companion object {
        private const val TAG = "Story Repository"

        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(apiService).also { INSTANCE = it }
            }
    }
}