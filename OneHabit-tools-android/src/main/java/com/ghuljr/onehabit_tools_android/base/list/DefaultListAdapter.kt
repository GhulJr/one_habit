package com.ghuljr.onehabit_tools_android.base.list

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import java.lang.RuntimeException

abstract class DefaultListAdapter<ITEM : UniqueItem<ITEM>>(
    private val itemManagers: List<HolderManager<ITEM>>
) : ListAdapter<ITEM, ItemViewHolder<ITEM>>(DefaultItemCallback<ITEM>()) {

    override fun getItemViewType(position: Int): Int {
        val currentItem = this.getItem(position)

        itemManagers.forEachIndexed { index, itemViewHolder ->
            if (itemViewHolder.isType(currentItem)) return index
        }

        throw RuntimeException("Unknown RecyclerView item type: $currentItem")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<ITEM> =
        itemManagers[viewType].createViewHolder(parent, LayoutInflater.from(parent.context))

    override fun onBindViewHolder(holder: ItemViewHolder<ITEM>, position: Int) {
        holder.bind(getItem(position))
    }
}