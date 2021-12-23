package com.ghuljr.onehabit_presenter.validator

import arrow.core.Either
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.utils.TestData
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class EmailValidatorTest {

    private lateinit var emailValidator: EmailValidatorImpl
    private lateinit var emailValidationObserver: TestObserver<Either<ValidationError, String>>

    private val testScheduler = TestScheduler()

    @Before
    fun setup() {
        emailValidator = EmailValidatorImpl(testScheduler)
        emailValidationObserver = emailValidator.validatedEmailEitherObservable.test()
    }

    @Test
    fun `when no email is provided, then no validation is done`() {
        testScheduler.triggerActions()
        emailValidationObserver.assertValueCount(0)
    }

    @Test
    fun `when empty email is provided, then return empty field error`() {
        emailValidator.emailChanged(TestData.EMPTY_FIELD)
        testScheduler.triggerActions()

        emailValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.swap().orNull()!! == ValidationError.EmptyField }
    }

    @Test
    fun `when invalid email is provided, then return invalid email format error`() {
        emailValidator.emailChanged(TestData.EMAIL_INVALID)
        testScheduler.triggerActions()

        emailValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.swap().orNull()!! == ValidationError.InvalidEmailFormat }
    }

    @Test
    fun `when correct email is provided, then return validated email`() {
        emailValidator.emailChanged(TestData.EMAIL_VALID)
        testScheduler.triggerActions()

        emailValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull()!! == TestData.EMAIL_VALID }
    }

    @Test
    fun `when the invalid value is provided, return error and correct it with valid email`() {
        emailValidator.emailChanged(TestData.EMAIL_INVALID)
        testScheduler.triggerActions()

        emailValidator.emailChanged(TestData.EMAIL_VALID)
        testScheduler.triggerActions()

        emailValidationObserver.assertValueCount(2)
            .assertValueAt(0) { it.swap().orNull()!! == ValidationError.InvalidEmailFormat }
            .assertValueAt(1) { it.orNull()!! == TestData.EMAIL_VALID }
    }
}