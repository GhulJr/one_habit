package com.ghuljr.onehabit_presenter.validator

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ghuljr.onehabit_error.PasswordError
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

interface PasswordValidator {
    val validatedPasswordEitherObservable: Observable<Either<ValidationError, String>>
    fun passwordChanged(password: String)

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
        val PASSWORD_REGEX = Regex.fromLiteral("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{$MIN_PASSWORD_LENGTH,}\$")
    }
}

class PasswordValidatorImpl @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler
) : PasswordValidator {

    private val passwordSubject = BehaviorSubject.create<String>()

    override val validatedPasswordEitherObservable: Observable<Either<ValidationError, String>> = passwordSubject
        .subscribeOn(computationScheduler)
        .map { it.validatePassword() }
        .replay(1)
        .refCount()

    override fun passwordChanged(password: String): Unit = passwordSubject.onNext(password)
}

private fun String.validatePassword(): Either<ValidationError, String> = when {
    isEmpty() -> ValidationError.EmptyField.left()
    length < PasswordValidator.MIN_PASSWORD_LENGTH -> PasswordError.ToShort(length, PasswordValidator.MIN_PASSWORD_LENGTH).left()
    !matches(PasswordValidator.PASSWORD_REGEX) -> PasswordError.InvalidFormat.left()
    else -> right()
}