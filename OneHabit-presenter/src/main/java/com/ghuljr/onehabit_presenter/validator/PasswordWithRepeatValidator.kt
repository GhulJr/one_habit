package com.ghuljr.onehabit_presenter.validator

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ghuljr.onehabit_error.PasswordError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.extension.onlyRight
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

interface PasswordWithRepeatValidator: PasswordValidator {

    val validatedRepeatPasswordEitherObservable: Observable<Either<PasswordError.NotMatching, Unit>>
    fun repeatPasswordChanged(repeat: String)
}

class PasswordWithRepeatValidatorImpl @Inject constructor(
    private val passwordValidator: PasswordValidator,
    @ComputationScheduler private val computationScheduler: Scheduler
) : PasswordWithRepeatValidator, PasswordValidator by passwordValidator {

    private val repeatSubject = BehaviorSubject.create<String>()

    override val validatedRepeatPasswordEitherObservable: Observable<Either<PasswordError.NotMatching, Unit>> = repeatSubject
        .withLatestFrom(validatedPasswordEitherObservable.onlyRight()) { repeated, password ->
            if(repeated == password) Unit.right() else PasswordError.NotMatching.left()
        }
        .subscribeOn(computationScheduler)
        .replay(1)
        .refCount()

    override fun repeatPasswordChanged(repeat: String) = repeatSubject.onNext(repeat)
}