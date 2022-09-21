package com.ghuljr.onehabit_data.domain

data class Milestone(
    val orderNumber: Int,
    val title: String,
    val goals: List<Goal>
) {

    /*TODO: add completion score
    * TODO: add info required to form summary
    * TODO: add state?
    **/
}