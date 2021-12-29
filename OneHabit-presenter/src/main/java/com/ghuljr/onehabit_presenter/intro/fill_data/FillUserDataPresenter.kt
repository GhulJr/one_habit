package com.ghuljr.onehabit_presenter.intro.fill_data

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@ActivityScope
class FillUserDataPresenter @Inject constructor(
): BasePresenter<FillUserDataView>() {

    override fun subscribeToView(view: FillUserDataView): Disposable = Disposable.empty()
}