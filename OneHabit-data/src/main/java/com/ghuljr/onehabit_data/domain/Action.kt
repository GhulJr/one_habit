package com.ghuljr.onehabit_data.domain


data class Action(
    val id: String,
    val goalId: String,
    val repeatCount: Int,
    val totalRepeats: Int,
    val customTitle: String?,
    val reminders: List<Long>?
)
