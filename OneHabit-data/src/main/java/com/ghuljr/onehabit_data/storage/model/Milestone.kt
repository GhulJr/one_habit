package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.BaseEntity

data class Milestone(
    val habitId: Int,
    val orderNumber: Int,
    val title: String,
    val goals: List<Goal>
) : BaseEntity() {

    /*TODO: add completion score
    * TODO: add info required to form summary
    * TODO: add state?
    **/
}