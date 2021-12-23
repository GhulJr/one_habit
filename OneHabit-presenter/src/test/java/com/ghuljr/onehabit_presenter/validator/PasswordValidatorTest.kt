package com.ghuljr.onehabit_presenter.validator

import arrow.core.Either
import com.ghuljr.onehabit_error.PasswordError
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.utils.TestData
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class PasswordValidatorTest {

    private lateinit var passwordValidator: PasswordValidatorImpl
    private lateinit var passwordValidationObserver: TestObserver<Either<ValidationError, String>>

    private val testScheduler = TestScheduler()

    @Before
    fun setup() {
        passwordValidator = PasswordValidatorImpl(testScheduler)
        passwordValidationObserver = passwordValidator.validatedPasswordEitherObservable.test()
    }

    @Test
    fun `when no password is provided, then no validation is done`() {
        testScheduler.triggerActions()
        passwordValidationObserver.assertValueCount(0)
    }

    @Test
    fun `when empty password is provided, then return empty field error`() {
        passwordValidator.passwordChanged(TestData.EMPTY_FIELD)
        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.swap().orNull()!! == ValidationError.EmptyField }
    }

    @Test
    fun `when too short password is provided, then return to short error error`() {
        passwordValidator.passwordChanged(TestData.PASSWORD_TO_SHORT)
        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(1)
            .assertValueAt(0) { (it.swap().orNull()!! as PasswordError.ToShort).provided == TestData.PASSWORD_TO_SHORT.length }
    }

    @Test
    fun `when password with no special sign nor number is provided, then return invalid password format error`() {
        passwordValidator.passwordChanged(TestData.PASSWORD_ONLY_LOWER_CASE)
        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.swap().orNull()!! == PasswordError.InvalidFormat }
    }

    @Test
    fun `when password with white space is provided, then return invalid password format error`() {
        passwordValidator.passwordChanged(TestData.PASSWORD_WHITE_SPACE)
        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.swap().orNull()!! == PasswordError.InvalidFormat }
    }

    @Test
    fun `when correct email is provided, then return validated email`() {
        passwordValidator.passwordChanged(TestData.PASSWORD_VALID)
        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull()!! == TestData.PASSWORD_VALID }
    }

    @Test
    fun `when the invalid value is provided, return error and correct it with valid email`() {
        passwordValidator.passwordChanged(TestData.PASSWORD_ONLY_LOWER_CASE)
        testScheduler.triggerActions()

        passwordValidator.passwordChanged(TestData.PASSWORD_VALID)
        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(2)
            .assertValueAt(0) { it.swap().orNull()!! == PasswordError.InvalidFormat }
            .assertValueAt(1) { it.orNull()!! == TestData.PASSWORD_VALID }
    }
}