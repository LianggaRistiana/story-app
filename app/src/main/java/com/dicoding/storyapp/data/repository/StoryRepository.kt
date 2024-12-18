package com.dicoding.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.local.pref.UserPreference
import com.dicoding.storyapp.data.local.entity.StoryEntity
import com.dicoding.storyapp.data.local.room.StoryDatabase
import com.dicoding.storyapp.data.paging.StoryRemoteMediator
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.remote.response.GeneralResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.Story
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesWithPaging(): LiveData<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreference),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStory()
            }
        ).liveData
    }

    fun getStories(location: String): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val token = userPreference.getUserSession().first().token
            val response = apiService.getStories(
                location = location,
                token = "Bearer $token",
            )
            val stories = response.listStory.orEmpty().filterNotNull()
            emit(Result.Success(stories))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, StoryResponse::class.java)
            Result.Error(errorBody.message!!)
        } catch (e: Exception) {
            Log.d(TAG, "getStories: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailStoryById(id: String): LiveData<Result<Story?>> = liveData {
        emit(Result.Loading)
        try {
            val token = userPreference.getUserSession().first().token
            val response = apiService.getDetailStoryById(id, "Bearer $token")
            val story = response.story
            emit(Result.Success(story))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, DetailStoryResponse::class.java)
            Result.Error(errorBody.message!!)
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
            val token = userPreference.getUserSession().first().token
            val response = apiService.addStory(multipartBody, requestBody, "Bearer $token")
            Log.d(TAG, "addStory: $response")
            Result.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, GeneralResponse::class.java)
            Result.Error(errorBody.message!!)
        } catch (e: Exception) {
            Log.d(TAG, "addStory: ${e.message.toString()} ")
            Result.Error(e.message.toString())
        }
    }

    companion object {
        private const val TAG = "Story Repository"

        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference, storyDatabase: StoryDatabase): StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(apiService, userPreference, storyDatabase).also { INSTANCE = it }
            }
    }
}