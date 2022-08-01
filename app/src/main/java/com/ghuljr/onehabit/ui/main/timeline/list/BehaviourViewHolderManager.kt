package com.ghuljr.onehabit.ui.main.timeline.list

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ItemTimelineBehaviourBinding
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_presenter.main.timeline.BehaviourItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class BehaviourViewHolderManager : ViewHolderManager(R.layout.item_timeline_behaviour) {

    override fun isType(item: UniqueItem): Boolean = item is BehaviourItem

    override fun createViewHolder(view: View) = object : ViewHolder<BehaviourItem>(view) {

        private val viewBind = ItemTimelineBehaviourBinding.bind(view)

        override fun bind(item: BehaviourItem) {
            viewBind.apply {
                val alpha = if (item.state == BehaviourItem.State.Future) 0.3f else 1.0f
                val iconRes = when (item.state) {
                    is BehaviourItem.State.Today -> R.drawable.ic_timeline_today
                    is BehaviourItem.State.Future -> R.drawable.ic_timeline_future
                    is BehaviourItem.State.Past.Failure -> R.drawable.ic_failed
                    is BehaviourItem.State.Past.NotSubmitted -> R.drawable.ic_warning
                    is BehaviourItem.State.Past.Success -> R.drawable.ic_checked
                }

                val iconTint = ContextCompat.getColorStateList(
                    viewBind.root.context, when (item.state) {
                        is BehaviourItem.State.Today,
                        is BehaviourItem.State.Future -> R.color.primary_black
                        is BehaviourItem.State.Past.Failure -> R.color.color_error
                        is BehaviourItem.State.Past.NotSubmitted -> R.color.color_warning
                        is BehaviourItem.State.Past.Success -> R.color.color_success
                    }
                )

                header.text = view.resources.getString(R.string.timeline_day_header, item.dayNumber.toString())
                title.text = item.title
                stateIcon.setImageResource(iconRes)
                stateIcon.imageTintList = iconTint

                root.forEach { view ->
                    if(view.id != R.id.icon_background)
                        view.alpha = alpha
                }
            }
        }
    }
}