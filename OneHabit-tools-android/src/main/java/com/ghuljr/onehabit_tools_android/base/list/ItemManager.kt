package com.ghuljr.onehabit_tools_android.base.list

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ItemManager<ITEM : UniqueItem<ITEM>, VB : ViewBinding> {
    abstract fun createViewHolder(context: Context): ItemViewHolder<ITEM, VB>
}


abstract class ItemViewHolder<ITEM : UniqueItem<ITEM>, VB : ViewBinding>(
    val viewBind: VB
) : RecyclerView.ViewHolder(viewBind.root) {

    abstract fun bind(item: ITEM)
}