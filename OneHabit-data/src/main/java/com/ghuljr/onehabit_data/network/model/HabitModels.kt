package com.ghuljr.onehabit_data.network.model

data class HabitResponse(
    val userId: String,
    val currentProgress: Int,
    val defaultProgressFactor: Int,
    val defaultRemindersMs: List<Long>?,
    val baseIntensity: Int,
    val desiredIntensity: Int,
    val title: String?,
    val description: String?,
    val type: String,
    val habitSubject: String,
    val settlingFormat: Int
)