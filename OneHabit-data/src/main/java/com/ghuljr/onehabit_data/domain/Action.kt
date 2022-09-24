package com.ghuljr.onehabit_data.domain


data class Action(
    val id: String,
    val repeatCount: Int?,
    val totalRepeats: Int?,
    val custom: Boolean,
    val finished: Boolean,
    val reminders: List<Long>?
)
