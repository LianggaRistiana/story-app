package com.dicoding.storyapp.data.local.pref

data class UserModel(
    val userId: String? = null,
    val userName: String? = null,
    val token: String? = null,
    val isLogin: Boolean = false
)