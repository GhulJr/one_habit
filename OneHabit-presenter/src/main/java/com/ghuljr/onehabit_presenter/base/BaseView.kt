package com.ghuljr.onehabit_presenter.base

interface BaseView<PRESENTER, VALUES> {
    fun getPresenter(): PRESENTER
    fun getInitialValues(): VALUES
}