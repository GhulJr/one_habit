package com.ghuljr.onehabit.ui.main.timeline.list

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTimelineGoalBinding
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_presenter.main.timeline.GoalItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class GoalViewHolderManager : ViewHolderManager(R.layout.item_timeline_goal) {

    override fun isType(item: UniqueItem): Boolean = item is GoalItem

    override fun createViewHolder(view: View) = object : ViewHolder<GoalItem>(view) {

        private val viewBind = ItemTimelineGoalBinding.bind(view)

        override fun bind(item: GoalItem) {
            viewBind.apply {
                val alpha = if (item.state == GoalItem.State.Future) 0.3f else 1.0f
                val iconRes = when (item.state) {
                    is GoalItem.State.Today -> R.drawable.ic_timeline_today
                    is GoalItem.State.Future -> R.drawable.ic_timeline_future
                    is GoalItem.State.Past.Failure -> R.drawable.ic_failed
                    is GoalItem.State.Past.Partially -> R.drawable.ic_warning
                    is GoalItem.State.Past.Success -> R.drawable.ic_checked
                }

                val iconTint = ContextCompat.getColorStateList(
                    viewBind.root.context, when (item.state) {
                        is GoalItem.State.Today,
                        is GoalItem.State.Future -> R.color.primary_black
                        is GoalItem.State.Past.Failure -> R.color.color_error
                        is GoalItem.State.Past.Partially -> R.color.color_warning
                        is GoalItem.State.Past.Success -> R.color.color_success
                    }
                )

                header.text = view.resources.getString(R.string.day_header, item.dayNumber.toString())
                stateIcon.setImageResource(iconRes)
                stateIcon.imageTintList = iconTint

                root.forEach { view ->
                    if(view.id != R.id.icon_background)
                        view.alpha = alpha
                }

                root.setOnClickListener { item.click() }
            }
        }
    }
}