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

        list.add(HeaderItem("I'm header, nice to meet you!"))
        for (i in 0..100) {
            list.add(BehaviourItem("$i I'm Behaviour! Really nice to meet you"))
        }
        list.add(SummaryItem("Summary item :)"))

        return list
    }
}