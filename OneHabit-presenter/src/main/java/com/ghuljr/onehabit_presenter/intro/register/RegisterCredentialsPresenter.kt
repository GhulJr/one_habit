package com.ghuljr.onehabit_presenter.intro.register

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.FragmentScope
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class RegisterCredentialsPresenter @Inject constructor(): BasePresenter<RegisterCredentialsView>() {
    override fun subscribeToView(view: RegisterCredentialsView): Disposable = Disposable.empty()
}