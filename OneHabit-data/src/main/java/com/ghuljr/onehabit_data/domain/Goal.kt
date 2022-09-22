package com.ghuljr.onehabit_data.domain

data class Goal(
    val title: String,
    val state: State,
    val actions: List<Action>
) {
    val totalScore: Int = actions.sumOf { it.scored }
    val maxScore = actions.sumOf { it.worth }

    enum class State {
         PENDING, ACTIVE, FAILED, PARTIAL, SUCCEED
    }
}