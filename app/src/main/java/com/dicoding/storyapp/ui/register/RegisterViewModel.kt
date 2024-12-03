package com.dicoding.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.data.remote.request.RegisterRequest
import com.dicoding.storyapp.data.remote.response.GeneralResponse
import com.dicoding.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: MutableLiveData<String> = _name

    private val _email = MutableLiveData<String>()
    val email: MutableLiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: MutableLiveData<String> = _password

    private val _registerProcessState = MutableLiveData<Result<GeneralResponse>?>()
    val registerProcessState: LiveData<Result<GeneralResponse>?> = _registerProcessState


    fun setName(name: String) {
        _name.value = name
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }


    fun register() {
        _registerProcessState.value = Result.Loading
        viewModelScope.launch {
            Log.e("RegisterViewModel", "register: ${name.value} ${email.value} ${password.value}")
            _registerProcessState.value = userRepository.register(
                RegisterRequest(
                    name.value ?: "",
                    email.value ?: "",
                    password.value ?: ""
                )
            )
        }
    }

    fun clearRegisterProcessState() {
        _registerProcessState.value = null
    }
}