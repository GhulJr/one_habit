package com.ghuljr.onehabit_tools_android.base.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolderManager<ITEM : UniqueItem>(@LayoutRes private val layoutRes: Int) {

    abstract fun isType(item: UniqueItem): Boolean

    abstract fun createViewHolder(view: View): ViewHolder<ITEM>

    fun createViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): ViewHolder<ITEM> = createViewHolder(createView(parent, inflater))

    private fun createView(parent: ViewGroup, inflater: LayoutInflater): View {
        return inflater.inflate(layoutRes, parent, false)
    }
}


abstract class ViewHolder<ITEM : UniqueItem>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: ITEM)
}