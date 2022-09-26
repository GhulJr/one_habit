package com.ghuljr.onehabit_data.domain

data class UserMetadata(
    val id: String,
    val habitId: String?,
    val milestoneId: String?,
    val goalId: String?,
    val extraHabits: List<String>?
)