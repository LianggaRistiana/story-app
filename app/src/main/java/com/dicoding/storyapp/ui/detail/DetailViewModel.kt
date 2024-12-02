package com.dicoding.storyapp.ui.detail

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoryById(id: String) = storyRepository.getDetailStoryById(id)
}