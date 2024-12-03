package com.dicoding.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: MutableLiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: MutableLiveData<String> = _password

    private val _loginProcessState = MutableLiveData<Result<LoginResponse>?>()
    val loginProcessState: LiveData<Result<LoginResponse>?> = _loginProcessState

    fun clearLoginProcessState() {
        _loginProcessState.value = null
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String){
        _password.value = password
    }

    fun login() {
        _loginProcessState.value = Result.Loading
        viewModelScope.launch {
            _loginProcessState.value = userRepository.login(
                LoginRequest(
                    email.value ?: "",
                    password.value ?: ""
                )
            )
        }
    }
}