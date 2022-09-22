package com.ghuljr.onehabit_data.domain


data class Action(
    val title: String,
    val description: String,
    val timeInTheDayMs: Int?, // Calculated time in 24h day, relative to the timezone we are in.
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