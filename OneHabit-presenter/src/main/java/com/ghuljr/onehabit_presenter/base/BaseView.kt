package com.ghuljr.onehabit_presenter.base

interface BaseView<PRESENTER, VALUES: InitialValuesHolder> {
    fun getPresenter(): PRESENTER
    fun getInitialValues(): VALUES
}