package com.ghuljr.onehabit_tools_android.base.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import java.lang.RuntimeException

class ItemListAdapter<ITEM : UniqueItem>(
    private val itemManagers: List<ViewHolderManager<ITEM>>
) : ListAdapter<ITEM, ViewHolder<ITEM>>(DefaultItemCallback<ITEM>()) {

    override fun getItemViewType(position: Int): Int {
        val currentItem = this.getItem(position)

        itemManagers.forEachIndexed { index, itemViewHolder ->
            if (itemViewHolder.isType(currentItem)) return index
        }

        throw RuntimeException("Unknown RecyclerView item type: $currentItem")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ITEM> =
        itemManagers[viewType].createViewHolder(parent, LayoutInflater.from(parent.context))

    override fun onBindViewHolder(holder: ViewHolder<ITEM>, position: Int) {
        holder.bind(getItem(position))
    }
}