package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.SerialDisposable
import javax.inject.Inject

@FragmentScope
class TodayPresenter @Inject constructor() : BasePresenter<TodayView>() {

    override fun subscribeToView(view: TodayView): Disposable = CompositeDisposable(
        Observable.just(getFakeData(view)).subscribe(view::submitItems)
    )

    private fun getFakeData(view: TodayView): List<TodayItem> = listOf(
        TodayActionItem(
            title = "Train hard!",
            time = "10:20",
            quantity = 2 to 3,
            onActionClick = { view.openDetails() }
        ),
        TodayActionItem(
            title = "Train hard!",
            time = "20:20",
            quantity = 3 to 3,
            onActionClick = { view.openDetails() }
        ),
        TodayActionItem(
            title = "Chocolate no!",
            time = null,
            quantity = null,
            onActionClick = { view.openDetails() }
        ),
        AddActionItem { view.addCustomAction() },
        DoneActionsHeaderItem,
        TodayActionFinishedItem(
            title = "Train hard!",
            time = "09:00",
            quantity = 1 to 3,
            onActionClick = { view.openDetails() }
        )
    )
}