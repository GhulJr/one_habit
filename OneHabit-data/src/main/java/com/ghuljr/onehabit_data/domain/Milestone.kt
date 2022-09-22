package com.ghuljr.onehabit_data.domain

data class Milestone(
    val title: String,
    val progress: Int,
    val goals: List<Goal>
) {

    val finished: Boolean = progress == 100


    /*TODO: add completion score
    * TODO: add info required to form summary
    * TODO: add state?
    **/
}