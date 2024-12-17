package com.dicoding.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.local.entity.StoryEntity
import com.dicoding.storyapp.data.repository.StoryRepository

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<StoryEntity>> =
        storyRepository.getStoriesWithPaging().cachedIn(viewModelScope)
}