package com.ghuljr.onehabit_tools_android.tool.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.SerialDisposable

abstract class BaseViewHolder<ITEM : BaseListItem<*>>(view: View) : RecyclerView.ViewHolder(view) {

    private val disposables = SerialDisposable()

    protected abstract fun bindStatic(item: ITEM)
    protected abstract fun subscribe(item: ITEM): Disposable

    fun bind(item: ITEM) {
        bindStatic(item)
        disposables.set(subscribe(item))
    }

    fun clear() {
        disposables.set(Disposable.empty())
    }

    interface Factory {
        fun create(parent: ViewGroup): BaseViewHolder<BaseListItem<*>>
        fun shouldHandle(item: BaseListItem<*>): Boolean
    }
}