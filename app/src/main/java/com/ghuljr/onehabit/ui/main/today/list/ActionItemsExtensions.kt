package com.ghuljr.onehabit.ui.main.today.list

import android.content.res.Resources
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit_tools.model.HabitTopic

internal fun Resources.generateTitle(habitTopic: HabitTopic, subject: String) = when(habitTopic) {
    HabitTopic.EAT -> getString(R.string.eat, subject)
    HabitTopic.NOT_EAT -> getString(R.string.not_eat, subject)
    HabitTopic.TRAIN -> getString(R.string.train, subject)
    HabitTopic.START_DOING -> getString(R.string.start_doing, subject)
    HabitTopic.STOP_DOING -> getString(R.string.stop_doing, subject)
}