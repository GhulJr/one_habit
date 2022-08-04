package com.ghuljr.onehabit_presenter.main.timeline

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.SerialDisposable
import javax.inject.Inject

@FragmentScope
class TimelinePresenter @Inject constructor(): BasePresenter<TimelineView>() {

    override fun subscribeToView(view: TimelineView): Disposable = SerialDisposable(
        Single.just(getFakeData()).subscribe { items ->
            view.submitItems(items)
        }
    )

    private fun getFakeData(): List<TimelineItem> {
        val list = mutableListOf<TimelineItem>()

        list.add(HeaderItem("Chocolate slayer!"))
        for (i in 1..14) {
            list.add(BehaviourItem(
                title = "Do not eat chocolate!",
                dayNumber = i,
                state = when {
                    i < 9 && i % 5 == 0 -> BehaviourItem.State.Past.NotSubmitted
                    i < 9 && i % 7 == 0 -> BehaviourItem.State.Past.Failure
                    i < 9 -> BehaviourItem.State.Past.Success
                    i == 9 -> BehaviourItem.State.Today
                    else -> BehaviourItem.State.Future
                }
            ))
        }
        list.add(SummaryItem(dayNumber = 9, totalDays = list.count { it is BehaviourItem }))
        return list
    }
}