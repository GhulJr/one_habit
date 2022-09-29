package com.ghuljr.onehabit_data.network.model

data class MilestoneResponse(
    val id: String,
    val userId: Int,
    val habitId: String,
    val intensity: Int,
    val orderNumber: Int,
    val resolved: Boolean
)

data class MilestoneRequest(
    val id: String,
    val userId: Int,
    val habitId: String,
    val intensity: Int
)