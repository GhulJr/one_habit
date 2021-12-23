package com.ghuljr.onehabit_presenter.intro.register

import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.validator.EmailValidator
import com.ghuljr.onehabit_presenter.validator.PasswordWithRepeatValidator
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class RegisterCredentialsPresenter @Inject constructor(
    private val emailValidator: EmailValidator,
    private val passwordWithRepeatValidator: PasswordWithRepeatValidator,
    @ComputationScheduler private val computationScheduler: Scheduler,
    @UiScheduler private val uiScheduler: Scheduler
): BasePresenter<RegisterCredentialsView>() {

    override fun subscribeToView(view: RegisterCredentialsView): Disposable = Disposable.empty()

}