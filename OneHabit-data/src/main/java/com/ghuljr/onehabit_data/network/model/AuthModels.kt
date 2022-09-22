package com.ghuljr.onehabit_data.network.model

import arrow.core.Option


data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)

data class UserAuthResponse(
    val userId: String,
    val email: String,
    val username: Option<String>,
    val isEmailVerified: Boolean
)