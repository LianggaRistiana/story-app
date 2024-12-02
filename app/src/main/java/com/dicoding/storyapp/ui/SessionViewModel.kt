package com.dicoding.storyapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.UserPreference
import kotlinx.coroutines.launch

class SessionViewModel(private val userPreference: UserPreference) : ViewModel() {
    private val _logOutProcess = MutableLiveData<Boolean>()
    val logOutProcess: MutableLiveData<Boolean> = _logOutProcess
    fun resetProcess() {
        _logOutProcess.value = false
    }

    fun getSession() = userPreference.getUserSession().asLiveData()


    fun logOut(){
        _logOutProcess.value = false
        viewModelScope.launch {
            userPreference.removeUserSession()
            _logOutProcess.value = true
        }
    }
}