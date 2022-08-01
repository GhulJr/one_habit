package com.ghuljr.onehabit_tools_android.base.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.ghuljr.onehabit_tools.base.list.UniqueItem
import java.lang.RuntimeException

abstract class ViewHolderManager(@LayoutRes private val layoutRes: Int) {

    abstract fun isType(item: UniqueItem): Boolean

    abstract fun createViewHolder(view: View): ViewHolder<*>

    fun createViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): ViewHolder<*> {
        return createViewHolder(createView(parent, inflater))
    }

    private fun createView(parent: ViewGroup, inflater: LayoutInflater): View {
        return inflater.inflate(layoutRes, parent, false)
    }

    @Suppress("UNCHECKED_CAST")
    abstract inner class ViewHolder<ITEM: UniqueItem>(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(item: UniqueItem) {
            if(isType(item)) bind(item as ITEM)
            else throw RuntimeException("Invalid item type: $item")
        }

        abstract fun bind(item: ITEM)
    }
}

