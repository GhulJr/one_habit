package com.ghuljr.onehabit.ui.main.today.list

import android.content.res.Resources
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit_tools.model.HabitTopic

internal fun Resources.generateTitle(habitTopic: HabitTopic, subject: String, exceeded: Boolean = false) = when(habitTopic) {
    HabitTopic.EAT -> getString(if(exceeded) R.string.eat_opposite else R.string.eat, subject)
    HabitTopic.NOT_EAT -> getString(if(exceeded)R.string.not_eat else  R.string.not_eat_opposite, subject)
    HabitTopic.TRAIN -> getString(if(exceeded) R.string.train_opposite else R.string.train, subject)
    HabitTopic.START_DOING -> getString(if(exceeded) R.string.start_doing_opposite else R.string.start_doing, subject)
    HabitTopic.STOP_DOING -> getString(if(exceeded) R.string.stop_doing_opposite else R.string.stop_doing, subject)
}