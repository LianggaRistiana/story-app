package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.local.dataStore
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(userPreference, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val userToken = runBlocking { userPreference.getUserSession().first().token  }
        val apiService = ApiConfig.getApiService(userToken!!)
        return StoryRepository.getInstance(apiService)
    }

    fun provideSessionPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }
}