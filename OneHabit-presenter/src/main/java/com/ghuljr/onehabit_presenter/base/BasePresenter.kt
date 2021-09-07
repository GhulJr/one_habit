package com.ghuljr.onehabit_presenter.base

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.SerialDisposable
import io.reactivex.rxjava3.processors.BehaviorProcessor

abstract class BasePresenter<VIEW, VALUES>: PresenterContract<VIEW, VALUES> {

    protected var view: VIEW? = null
    protected var initialValues: VALUES? = null
    protected var disposable: SerialDisposable = SerialDisposable()

    override fun attach(view: VIEW, initialValues: VALUES?) {
        this.view = view
        this.initialValues = initialValues
        onViewAttached()
    }

    override fun detach() {
        view = null
        initialValues = null
        onViewDetached()
    }

    //TODO: create own @CallSuper annotation
    override fun onViewAttached() { disposable.set(CompositeDisposable()) }
    override fun onViewDetached() { disposable.set(CompositeDisposable()) }
}

interface PresenterContract<VIEW, VALUES> {
    fun attach(view: VIEW, initialValues: VALUES?)
    fun detach()
    fun onViewAttached()
    fun onViewDetached()
}