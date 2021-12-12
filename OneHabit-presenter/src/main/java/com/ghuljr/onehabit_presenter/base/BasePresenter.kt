package com.ghuljr.onehabit_presenter.base

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.SerialDisposable
import io.reactivex.rxjava3.processors.BehaviorProcessor

abstract class BasePresenter<VIEW>: PresenterContract<VIEW> {

    protected var view: VIEW? = null
    protected var disposable: SerialDisposable = SerialDisposable()

    override fun attach(view: VIEW) {
        this.view = view
        onViewAttached()
    }

    override fun detach() {
        view = null
        onViewDetached()
    }

    //TODO: create own @CallSuper annotation
    override fun onViewAttached() { disposable.set(CompositeDisposable()) }
    override fun onViewDetached() { disposable.set(CompositeDisposable()) }
}

interface PresenterContract<VIEW> {
    fun attach(view: VIEW)
    fun detach()
    fun onViewAttached()
    fun onViewDetached()
}