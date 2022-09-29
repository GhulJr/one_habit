package com.ghuljr.onehabit_data.network.model

data class ActionResponse(
    val id: String,
    val remindersAtMs: List<Long>?,
    val currentRepeat: Int,
    val totalRepeats: Int,
    val customTitle: String?
)

data class ActionRequest(
    val userId: String,
    val goalId: String,
    val remindersAtMs: List<Long>,
    val currentRepeat: Int,
    val totalRepeats: Int,
    val customTitle: String?
) {

    fun toActionResponse(id: String) = ActionResponse(
        id = id,
        remindersAtMs = remindersAtMs,
        currentRepeat = currentRepeat,
        totalRepeats = totalRepeats,
        customTitle = customTitle
    )
}