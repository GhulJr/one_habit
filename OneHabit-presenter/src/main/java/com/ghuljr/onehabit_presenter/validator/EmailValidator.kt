package com.ghuljr.onehabit_presenter.validator

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

interface EmailValidator {
    val validatedEmailEitherObservable: Observable<Either<ValidationError, String>>
    fun emailChanged(email: String)

    companion object {
        val EMAIL_REGEX = Regex.fromLiteral("^(.+)@(.+)\$")
    }
}

class EmailValidatorImpl @Inject constructor(
    @ComputationScheduler computationScheduler: Scheduler
) : EmailValidator {

    private val emailSubject = BehaviorSubject.create<String>()

    override val validatedEmailEitherObservable: Observable<Either<ValidationError, String>> = emailSubject
        .subscribeOn(computationScheduler)
        .map { it.validateEmail() }
        .replay(1)
        .refCount()

    override fun emailChanged(email: String): Unit = emailSubject.onNext(email)
}

private fun String.validateEmail(): Either<ValidationError, String> = when {
    isEmpty() -> ValidationError.EmptyField.left()
    !matches(EmailValidator.EMAIL_REGEX) -> ValidationError.InvalidEmailFormat.left()
    else -> right()
}

