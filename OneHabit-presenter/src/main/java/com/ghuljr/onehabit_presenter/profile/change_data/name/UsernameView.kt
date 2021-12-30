package com.ghuljr.onehabit_presenter.profile.change_data.name

import arrow.core.Option
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.base.BaseView
import io.reactivex.rxjava3.core.Observable

interface UsernameView : BaseView<UsernamePresenter> {

    fun usernameChangedObservable(): Observable<String>
    fun finishClickedObservable(): Observable<Unit>
    fun finish()
    fun handleValidationError(error: Option<ValidationError.EmptyField>)
    fun handleChangeNameEvent(event: Option<BaseEvent>)
}