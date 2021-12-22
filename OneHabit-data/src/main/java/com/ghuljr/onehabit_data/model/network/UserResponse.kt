package com.ghuljr.onehabit_data.model.network

import arrow.core.Option

data class UserResponse(
    val userId: String,
    val email: String,
    val username: Option<String>
)