package com.ghuljr.onehabit.ui.main.today.list

import android.view.View
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import com.ghuljr.onehabit_tools_android.base.list.ViewHolderManager

class TodayActionViewHolderManager : ViewHolderManager(R.layout.item_today_action) {

    override fun isType(item: UniqueItem): Boolean = item is TodayActionViewHolderManager

    override fun createViewHolder(view: View): ViewHolder<*> {
        TODO("Not yet implemented")
    }
}