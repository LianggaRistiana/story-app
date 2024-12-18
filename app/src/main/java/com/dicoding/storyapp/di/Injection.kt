package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.local.pref.UserPreference
import com.dicoding.storyapp.data.local.pref.dataStore
import com.dicoding.storyapp.data.local.room.StoryDatabase
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(userPreference, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val storyDatabase = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService, userPreference, storyDatabase)
    }

    fun provideSessionPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }
}