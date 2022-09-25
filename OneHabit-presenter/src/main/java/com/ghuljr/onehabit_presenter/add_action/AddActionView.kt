package com.ghuljr.onehabit_presenter.add_action

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface AddActionView : BaseView<AddActionPresenter> {

    fun actionNameChangedObservable(): Observable<String>
    fun handleEvent(event: Option<BaseEvent>)
    fun close()
}