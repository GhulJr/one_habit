package com.ghuljr.onehabit_data.domain

data class Goal(
    val remindAtMs: Long,
    val actions: List<Action>
)