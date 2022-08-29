package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_tools.base.list.UniqueItem

sealed interface TodayItem : UniqueItem

data class TodayAction(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }
}

data class CustomAction(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }
}

data class FinishedItem(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }
}

data class TodayHeaderItem(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }
}

data class TodayTimestampItem(val w: String) : TodayItem {
    override fun theSame(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun matches(item: UniqueItem): Boolean {
        TODO("Not yet implemented")
    }

}

object AddAction : TodayItem {
    override fun theSame(item: UniqueItem): Boolean = item is AddAction
    override fun matches(item: UniqueItem): Boolean = item == this
}

