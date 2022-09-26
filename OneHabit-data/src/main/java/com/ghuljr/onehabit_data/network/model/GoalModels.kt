package com.ghuljr.onehabit_data.network.model

data class GoalResponse(
    val goalId: String,
    val userId: String,
    val milestoneId: String,
    val remindAtMs: Long?
)