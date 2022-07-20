package com.ghuljr.onehabit_tools_android.base.list

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class DefaultListAdapter<ITEM: UniqueItem<ITEM>, HOLDER: RecyclerView.ViewHolder> : ListAdapter<ITEM, HOLDER>(DefaultItemCallback<ITEM>())