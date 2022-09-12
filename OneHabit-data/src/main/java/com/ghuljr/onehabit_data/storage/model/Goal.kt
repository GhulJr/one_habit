package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.BaseEntity

data class Goal(
    val milestoneId: Int,
    val orderNumber: Int,
    val todayMotto: String,
    val actions: List<Action>,
) : BaseEntity() {

    /*TODO: create properties to calculate score based on actions calculation
    * val totalScore: Int
    * val finishState: FinishState
    * val dayTimeMs: Int
    **/

    // TODO: it might be only needed for visual model. Instead I could store
    enum class FinishState {
        SUCCESS, REQUIRES_ACTION, FAILED, ACTIVE, FUTURE
    }
}