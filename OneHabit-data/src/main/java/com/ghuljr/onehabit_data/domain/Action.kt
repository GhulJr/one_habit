package com.ghuljr.onehabit_data.domain


data class Action(
    val currentRepeat: Int?,
    val repeats: Int?,
    val isCustom: Boolean,
    val isFinished: Boolean
)
