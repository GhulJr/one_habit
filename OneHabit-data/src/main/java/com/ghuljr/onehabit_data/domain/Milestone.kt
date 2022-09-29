package com.ghuljr.onehabit_data.domain

data class Milestone(
    val id: String,
    val userId: String,
    val intensity: Int,
    val resolvedAt: Long?
)