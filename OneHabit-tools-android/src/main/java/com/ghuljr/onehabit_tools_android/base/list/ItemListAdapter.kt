package com.ghuljr.onehabit_tools_android.base.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import java.lang.RuntimeException

class ItemListAdapter(
    private vararg val itemManagers: ViewHolderManager
) : ListAdapter<UniqueItem, ViewHolderManager.ViewHolder<*>>(DefaultItemCallback()) {

    override fun getItemViewType(position: Int): Int {
        val currentItem = this.getItem(position)

        itemManagers.forEachIndexed { index, itemViewHolder ->
            if (itemViewHolder.isType(currentItem)) return index
        }

        throw RuntimeException("Unknown RecyclerView item type: $currentItem")
    }

    override fun onBindViewHolder(holder: ViewHolderManager.ViewHolder<*>, position: Int) {
        holder.bindItem(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderManager.ViewHolder<out UniqueItem> {
        return itemManagers[viewType].createViewHolder(parent, LayoutInflater.from(parent.context))
    }
}