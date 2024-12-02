package com.dicoding.storyapp.ui.home

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories() = storyRepository.getStories("0")
}