package com.ghuljr.onehabit.ui.main.today.list

import android.content.res.Resources
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit_tools.model.HabitTopic

fun HabitTopic.generateTitleWithOpposition(
    resources: Resources,
    subject: String,
    exceeded: Boolean = false,
    weekly: Boolean = false
) = resources.run {
    if (weekly)
        when (this@generateTitleWithOpposition) {
            HabitTopic.EAT -> getString(if (exceeded) R.string.eat_param else R.string.eat_opposite, subject)
            HabitTopic.NOT_EAT -> getString(if (exceeded) R.string.not_eat_param else R.string.not_eat_opposite, subject)
            HabitTopic.TRAIN -> getString(if (exceeded) R.string.train_param else R.string.train_opposite, subject)
            HabitTopic.START_DOING -> getString(if (exceeded) R.string.start_doing_param else R.string.start_doing_opposite, subject)
            HabitTopic.STOP_DOING -> getString(if (exceeded) R.string.stop_doing_param else R.string.stop_doing_opposite, subject)
        }
    else generateTitle(resources, subject)
}

fun HabitTopic.generateTitle(resources: Resources, subject: String) = resources.run {
    when (this@generateTitle) {
        HabitTopic.EAT -> getString(R.string.eat_param, subject)
        HabitTopic.NOT_EAT -> getString(R.string.not_eat_param, subject)
        HabitTopic.TRAIN -> getString(R.string.train_param, subject)
        HabitTopic.START_DOING -> getString(R.string.start_doing_param, subject)
        HabitTopic.STOP_DOING -> getString(R.string.stop_doing_param, subject)
    }
}