package com.ghuljr.onehabit_tools_android.base.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class HolderManager<ITEM : UniqueItem<ITEM>>(@LayoutRes private val layoutRes: Int) {
    abstract fun isType(comparable: UniqueItem<*>): Boolean

    abstract fun createViewHolder(view: View): ItemViewHolder<ITEM>

    fun createViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): ItemViewHolder<ITEM> {
        return createViewHolder(createView(parent, inflater))
    }

    private fun createView(parent: ViewGroup, inflater: LayoutInflater): View {
        return inflater.inflate(layoutRes, parent, false)
    }
}


abstract class ItemViewHolder<ITEM : UniqueItem<ITEM>>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: ITEM)
}