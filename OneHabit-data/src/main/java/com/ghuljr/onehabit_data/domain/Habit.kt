package com.ghuljr.onehabit_data.domain

data class Habit(
    val title: String,
    val description: String,
    val milestones: List<Milestone>,
    val baselineScore: Int,
    val finishScore: Int,
   // val progressFactor: Int - TODO: for now abandon the idea of making generic tasks with scaling factor of complexity
) {
    /*TODO: add state (something like ACTIVE, WAITING, FINISHED)
    * TODO: think how to generate list of high-end activities to hook up to other, ongoing habits
    * */
}