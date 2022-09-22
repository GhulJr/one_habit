package com.ghuljr.onehabit_data.domain

data class Goal(
    val orderNumber: Int,
    val todayMotto: String,
    val actions: List<Action>,
    val finishedState: FinishState
) {

    val totalScore = actions.sumOf { it.score }

    // TODO: it might be only needed for visual model. Instead I could store
    enum class FinishState {
        SUCCESS, REQUIRES_ACTION, FAILED, ACTIVE, FUTURE
    }
}