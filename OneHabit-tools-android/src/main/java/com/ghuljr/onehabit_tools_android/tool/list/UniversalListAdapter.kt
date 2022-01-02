package com.ghuljr.onehabit_tools_android.tool.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class UniversalListAdapter(
    private val viewHolderFactories: List<BaseViewHolder.Factory>
) : ListAdapter<BaseListItem<*>, BaseViewHolder<BaseListItem<*>>>(ITEM_DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return viewHolderFactories.indexOfFirst { it.shouldHandle(item) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BaseListItem<*>> = viewHolderFactories[viewType].create(parent)

    override fun onBindViewHolder(holder: BaseViewHolder<BaseListItem<*>>, position: Int) = holder.bind(getItem(position))

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<BaseListItem<*>>) {
        super.onViewDetachedFromWindow(holder)
        holder.clear()
    }

    companion object {
        val ITEM_DIFF_CALLBACK = object : DiffUtil.ItemCallback<BaseListItem<*>>() {

            override fun areItemsTheSame(
                oldItem: BaseListItem<*>,
                newItem: BaseListItem<*>
            ): Boolean =
                oldItem.areItemsTheSame(newItem)


            override fun areContentsTheSame(
                oldItem: BaseListItem<*>,
                newItem: BaseListItem<*>
            ): Boolean =
                newItem.areContentsTheSame(newItem)

        }
    }
}