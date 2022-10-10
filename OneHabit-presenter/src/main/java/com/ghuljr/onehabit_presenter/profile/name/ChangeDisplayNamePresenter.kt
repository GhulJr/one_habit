package com.ghuljr.onehabit_presenter.profile.name

import arrow.core.left
import arrow.core.none
import arrow.core.some
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import com.ghuljr.onehabit_presenter.base.BasePresenter
import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.UiScheduler
import com.ghuljr.onehabit_tools.extension.mapLeft
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@ActivityScope
class ChangeDisplayNamePresenter @Inject constructor(
    @UiScheduler private val uiScheduler: Scheduler,
    @ComputationScheduler private val computationScheduler: Scheduler,
    private val loggedInUserRepository: LoggedInUserRepository
) : BasePresenter<ChangeDisplayNameView>() {

    private val setNewNameSubject = PublishSubject.create<Unit>()
    private val newNameSubject = BehaviorSubject.create<String>()

    override fun subscribeToView(view: ChangeDisplayNameView): Disposable = CompositeDisposable(
        setNewNameSubject
            .withLatestFrom(newNameSubject) { _, newName -> newName }
            .switchMap { newName ->
                loggedInUserRepository.setName(newName)
                    .toObservable()
                    .mapLeft { it as BaseEvent }
                    .startWithItem(LoadingEvent.left())
            }
            .observeOn(uiScheduler)
            .subscribe {
                it.fold(
                    ifRight = {
                        view.handleEvent(none())
                        view.close()
                    },
                    ifLeft = { view.handleEvent(it.some()) }
                )
            }
    )

    fun setNewName() = setNewNameSubject.onNext(Unit)
    fun nameChanged(name: String) = newNameSubject.onNext(name)
}