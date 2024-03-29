package com.ghuljr.onehabit_data.network.model

data class HabitResponse(
    val userId: String,
    val id: String,
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

data class HabitRequest(
    val userId: String,
    val defaultProgressFactor: Int,
    val baseIntensity: Int,
    val desiredIntensity: Int,
    val topic: String,
    val habitSubject: String,
    val frequency: Int
)