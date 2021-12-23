package com.ghuljr.onehabit_presenter.validator

import arrow.core.Either
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

interface EmailValidator {
    val validatedEmailEitherObservable: Observable<Either<ValidationError, String>>
    fun emailChanged(email: String)
}

internal class EmailValidatorImpl @Inject constructor(
    @ComputationScheduler computationScheduler: Scheduler
) : EmailValidator {

    private val emailSubject = BehaviorSubject.create<String>()

    override val validatedEmailEitherObservable: Observable<Either<ValidationError, String>>
        get() = TODO("Not yet implemented")

    override fun emailChanged(email: String) {
        TODO("Not yet implemented")
    }
}

