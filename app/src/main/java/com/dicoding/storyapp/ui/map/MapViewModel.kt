package com.dicoding.storyapp.ui.map

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class MapViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories() = storyRepository.getStories("1")
}