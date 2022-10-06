package com.ghuljr.onehabit_data.domain

import com.ghuljr.onehabit_tools.model.HabitTopic

data class Habit(
    val userId: String,
    val id: String,
    val currentProgress: Int,
    val defaultProgressFactor: Int,
    val defaultRemindersMs: List<Long>?,
    val baseIntensity: Int,
    val desiredIntensity: Int,
    val title: String?,
    val description: String?,
    val topic: HabitTopic,
    val habitSubject: String,
    val frequency: Int
)