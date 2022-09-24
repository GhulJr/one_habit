package com.ghuljr.onehabit_data.domain


data class Action(
    val id: String,
    val currentRepeat: Int?,
    val repeats: Int?,
    val custom: Boolean,
    val finished: Boolean,
    val reminders: List<Long>?
)
