package com.ghuljr.onehabit_presenter.base

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.SerialDisposable
import io.reactivex.rxjava3.processors.BehaviorProcessor

abstract class BasePresenter<VIEW> {

    private var view: VIEW? = null
    protected var disposable: SerialDisposable = SerialDisposable()

    fun attach(view: VIEW) {
        this.view = view
        onViewAttached()
    }

    fun detach() {
        view = null
        onViewDetached()
    }

     fun onViewAttached() { disposable.set(subscribeToView(view!!)) }
     fun onViewDetached() { disposable.set(CompositeDisposable()) }

    abstract fun subscribeToView(view: VIEW): Disposable
}