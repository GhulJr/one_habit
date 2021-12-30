package com.ghuljr.onehabit_presenter.profile.change_data.name

import arrow.core.none
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_presenter.validator.FieldValidator
import com.ghuljr.onehabit_tools.di.FragmentScope
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.leftAsEvent
import com.ghuljr.onehabit_tools.extension.leftToOption
import com.ghuljr.onehabit_tools.extension.onlyRight
import com.ghuljr.onehabit_tools.extension.toObservableWithLoading
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@FragmentScope
class UsernamePresenter @Inject constructor(
    private val loggedInUserRepository: LoggedInUserRepository,
    private val usernameValidator: FieldValidator,
    @UiScheduler private val uiScheduler: Scheduler,
) : BasePresenter<UsernameView>() {

    override fun subscribeToView(view: UsernameView): Disposable {
        val finishClicked = view.finishClickedObservable()

        return CompositeDisposable(
            view.usernameChangedObservable()
                .observeOn(uiScheduler)
                .subscribe {
                    view.handleValidationError(none())
                    usernameValidator.fieldChanged(it)
                },
            finishClicked
                .switchMapSingle {
                    usernameValidator.validatedFieldEitherObservable
                        .firstOrError()
                        .leftToOption()
                }
                .observeOn(uiScheduler)
                .subscribe { view.handleValidationError(it) },
            finishClicked
                .switchMap {
                    usernameValidator.validatedFieldEitherObservable
                        .firstOrError()
                        .onlyRight()
                        .flatMapObservable {
                            loggedInUserRepository.changeDisplayName(it)
                                .leftAsEvent()
                                .toObservableWithLoading()
                        }
                }
                .observeOn(uiScheduler)
                .subscribe {
                    view.handleChangeNameEvent(it.swap().orNone())
                    it.map { view.finish() }
                },
            usernameValidator.validatedFieldEitherObservable.subscribe()
        )
    }
}