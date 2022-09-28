package com.ghuljr.onehabit_data.domain

data class Goal(
    val id: String,
    val userId: String,
    val milestoneId: String,
    val remindAtMs: Long?,
    val dayNumber: Long,
    val finished: Boolean
    )