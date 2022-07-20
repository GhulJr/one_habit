package com.ghuljr.onehabit_tools_android.base.list

import androidx.recyclerview.widget.DiffUtil

class DefaultItemCallback<T: UniqueItem<T>> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = newItem.theSame(oldItem)
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = newItem.matches(oldItem)
}