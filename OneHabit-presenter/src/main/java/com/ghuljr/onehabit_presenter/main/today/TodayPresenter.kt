package com.ghuljr.onehabit_presenter.main.today

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class TodayPresenter @Inject constructor(): BasePresenter<TodayView>() {

    override fun subscribeToView(view: TodayView): Disposable {
        return Disposable.empty()
    }
}