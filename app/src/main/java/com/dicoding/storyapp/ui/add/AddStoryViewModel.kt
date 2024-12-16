package com.dicoding.storyapp.ui.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.data.remote.response.GeneralResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.helper.util.reduceFileImage
import com.dicoding.storyapp.helper.util.uriToFile
import kotlinx.coroutines.launch

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImageUri

    private val _currentDescription = MutableLiveData<String?>()

    private val _uploadState = MutableLiveData<Result<GeneralResponse>>()
    val uploadState: MutableLiveData<Result<GeneralResponse>> = _uploadState

    fun setCurrentDescription(description: String?) {
        _currentDescription.value = description
    }

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun uploadStory(context: Context) {
        _uploadState.value = Result.Loading
        currentImageUri.value?.let { uri ->
            viewModelScope.launch {
                _uploadState.value = storyRepository.addStory(
                    uriToFile(uri, context).reduceFileImage(),
                    _currentDescription.value.toString()
                )
            }
        } ?: run {
            _uploadState.value = Result.Error("Image not found")
        }
    }
}