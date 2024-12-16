package com.dicoding.storyapp.helper.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.ui.SessionViewModel
import com.dicoding.storyapp.ui.add.AddStoryViewModel
import com.dicoding.storyapp.ui.detail.DetailViewModel
import com.dicoding.storyapp.ui.home.HomeViewModel
import com.dicoding.storyapp.ui.login.LoginViewModel
import com.dicoding.storyapp.ui.map.MapViewModel
import com.dicoding.storyapp.ui.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            return SessionViewModel(userPreference) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(storyRepository) as T
        }else if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(storyRepository) as T
        }

        throw IllegalArgumentException("Unknown Model : ${modelClass.name}")
    }


    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideStoryRepository(context),
                    Injection.provideSessionPreference(context)
                )
            }.also { INSTANCE = it }
    }
}