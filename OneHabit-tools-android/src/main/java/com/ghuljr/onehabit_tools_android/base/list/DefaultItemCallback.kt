package com.ghuljr.onehabit_tools_android.base.list

import androidx.recyclerview.widget.DiffUtil
import com.ghuljr.onehabit_tools.base.list.UniqueItem

class DefaultItemCallback : DiffUtil.ItemCallback<UniqueItem>() {

    override fun areItemsTheSame(oldItem: UniqueItem, newItem: UniqueItem): Boolean = newItem.theSame(oldItem)
    override fun areContentsTheSame(oldItem: UniqueItem, newItem: UniqueItem): Boolean = newItem.matches(oldItem)
}