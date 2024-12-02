package com.dicoding.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import com.dicoding.storyapp.data.remote.Result

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: MutableLiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: MutableLiveData<String> = _password

    private val _formValid = MutableLiveData(false)
    val formValid: LiveData<Boolean> = _formValid

    private val _loginProcessState = MutableLiveData<Result<LoginResponse>?>()
    val loginProcessState: LiveData<Result<LoginResponse>?> = _loginProcessState

    init {
        isFormValid()
    }

    fun clearLoginProcessState() {
        _loginProcessState.value = null
    }

    fun isFormValid() {
        _formValid.value = _email.value.toString().isEmailValid() && _password.value.toString().isPasswordValid()
    }

    fun setEmail(email: String): Boolean {
        _email.value = email
        isFormValid()
        return email.isEmailValid()
    }

    fun setPassword(password: String) : Boolean {
        _password.value = password
        isFormValid()
        return password.isPasswordValid()
    }

    fun login() {
        _loginProcessState.value = Result.Loading
        viewModelScope.launch {
            _loginProcessState.value = userRepository.login(LoginRequest(email.value.toString(), password.value.toString()))
        }
    }

    private fun String.isEmailValid(): Boolean {
        return !this.isNullOrEmpty() && this.length >= 6
    }
    private fun String.isPasswordValid(): Boolean {
        return !this.isNullOrEmpty() && this.length >= 8
    }
}