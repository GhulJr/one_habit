package com.ghuljr.onehabit_presenter.base

interface BaseView<PRESENTER> {
    fun getPresenter(): PRESENTER
    fun getInitialValues(): InitialValuesHolder
}