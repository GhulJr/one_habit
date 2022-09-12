package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.BaseEntity

data class Habit(
    val title: String,
    val description: String,
    val milestones: List<Milestone>
) : BaseEntity() {
    /*TODO: add state (something like ACTIVE, WAITING, FINISHED)
    * TODO: think how to generate list of high-end activities to hook up to other, ongoing habits
    * */
}