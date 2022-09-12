package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.BaseEntity

data class Action(
    val todayId: Int,
    val title: String,
    val description: String,
    val timeMs: Int?,
    val quantityNumber: Int?,
    val maxQuantity: Int?,
    val isCustom: Boolean,
    val state: State
) : BaseEntity() {

    enum class State {
        PENDING, REQUIRE_CONFIRMATION, FINISHED
    }
}

/* TODO: add score to have base for calculations of Action progress */