package com.ghuljr.onehabit_data.network.model

data class ActionResponse(
    val id: String,
    val remindersAtMs: List<Long>?,
    val currentRepeat: Int,
    val totalRepeats: Int
)