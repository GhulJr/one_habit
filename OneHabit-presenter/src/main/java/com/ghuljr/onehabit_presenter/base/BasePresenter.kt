package com.ghuljr.onehabit_presenter.base

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.SerialDisposable

abstract class BasePresenter<VIEW, VALUES: InitialValuesHolder>: PresenterContract<VIEW, VALUES> {

    protected var view: VIEW? = null
    protected var initialValues: VALUES? = null
    protected var disposable: SerialDisposable = SerialDisposable()

    //TODO: abstract state representing behaviour processor

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

interface PresenterContract<VIEW, VALUES : InitialValuesHolder> {
    fun attach(view: VIEW, initialValues: VALUES?)
    fun detach()
    fun onViewAttached()
    fun onViewDetached()
}

interface InitialValuesHolder