package com.ghuljr.onehabit_tools_android.tool

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDivider(val spanCount: Int, private val dividerWidth: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        val left = if(position % spanCount == 0) 0 else dividerWidth / 2
        val right = if(position % spanCount == spanCount - 1) 0 else dividerWidth / 2
        val top = if(position < spanCount) 0 else dividerWidth / 2
        val bottom = if(position >= state.itemCount - spanCount) 0 else dividerWidth / 2

        outRect.set(left, top, right, bottom)
    }
}