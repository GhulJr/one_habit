package com.ghuljr.onehabit_data.domain


data class Action(
    val id: String,
    val goalId: String,
    val repeatCount: Int,
    val totalRepeats: Int,
    val custom: Boolean,
    val reminders: List<Long>?
)
