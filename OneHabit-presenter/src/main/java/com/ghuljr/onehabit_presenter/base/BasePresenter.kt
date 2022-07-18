package com.ghuljr.onehabit_presenter.base

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.SerialDisposable
import io.reactivex.rxjava3.processors.BehaviorProcessor

abstract class BasePresenter<VIEW>: PresenterContract<VIEW> {

    private var view: VIEW? = null
    protected var disposable: SerialDisposable = SerialDisposable()

    final override fun attach(view: VIEW) {
        this.view = view
        onViewAttached()
    }

    final override fun detach() {
        view = null
        onViewDetached()
    }

    final override fun onViewAttached() { disposable.set(subscribeToView(view!!)) }
    override fun onViewDetached() { disposable.set(CompositeDisposable()) }

    abstract fun subscribeToView(view: VIEW): Disposable
}

interface PresenterContract<VIEW> {
    fun attach(view: VIEW)
    fun detach()
    fun onViewAttached()
    fun onViewDetached()
}