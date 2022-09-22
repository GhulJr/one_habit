package com.ghuljr.onehabit_data.domain

data class Habit(
    val title: String,
    val description: String,
    val milestones: List<Milestone>,
    val progress: Int
    )