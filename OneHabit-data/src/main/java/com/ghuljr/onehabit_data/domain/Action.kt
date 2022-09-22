package com.ghuljr.onehabit_data.domain


data class Action(
    val title: String,
    val description: String,
    val timeRelativeToDayMs: Int?, // Calculated time in 24h day, relative to the timezone we are in.
    val currentRepeat: Int?,
    val repeats: Int?,
    val isCustom: Boolean,
    val finished: Boolean,
    val worth: Int
) {

    val scored = if(finished) worth else 0
}

/*TODO: how to model finish state and the cost points of the action*/