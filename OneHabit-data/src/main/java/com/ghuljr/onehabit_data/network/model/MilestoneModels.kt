package com.ghuljr.onehabit_data.network.model

data class MilestoneResponse(
    val id: String,
    val userId: String,
    val intensity: Int,
    val orderNumber: Int,
    val resolved: Boolean
)

data class MilestoneRequest(
    val userId: String,
    val habitId: String,
    val intensity: Int
)