package com.ghuljr.onehabit_data.network.model

data class UserMetadataResponse(
    val userId: String,
    val habitId: String?,
    val milestoneId: String?,
    val goalId: String?,
    val topTierHabitsIds: List<String>?
)