package com.ghuljr.onehabit_presenter.main

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject constructor() : BasePresenter<MainView>() {

    override fun subscribeToView(view: MainView): Disposable = Disposable.empty()
}