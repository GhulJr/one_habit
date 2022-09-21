package com.ghuljr.onehabit_data.domain


data class Action(
    val title: String,
    val description: String,
    val timeMs: Int?,
    val quantityNumber: Int?,
    val maxQuantity: Int?,
    val isCustom: Boolean,
    val state: State,
    val score: Int
) {

    enum class State {
        PENDING, REQUIRE_CONFIRMATION, FINISHED
    }
}