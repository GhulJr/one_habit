package com.ghuljr.onehabit_data.network.model

data class ActionResponse(
    val remindersAtMs: List<Long>?,
    val currentRepeat: Int,
    val totalRepeats: Int
)