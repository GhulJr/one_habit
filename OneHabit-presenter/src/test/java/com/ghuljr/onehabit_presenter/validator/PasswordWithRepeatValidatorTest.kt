package com.ghuljr.onehabit_presenter.validator

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ghuljr.onehabit_error.PasswordError
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_presenter.utils.TestData
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class PasswordWithRepeatValidatorTest {

    private lateinit var passwordWithRepeatValidator: PasswordWithRepeatValidatorImpl
    private lateinit var passwordValidationObserver: TestObserver<Either<PasswordError.NotMatching, Unit>>

    private val passwordSubject = BehaviorSubject.create<Either<ValidationError, String>>()
    private val passwordValidator = mockk<PasswordValidator>(relaxUnitFun = true)
    private val testScheduler = TestScheduler()

    @Before
    fun setup() {

        every { passwordValidator.validatedPasswordEitherObservable } returns passwordSubject

        passwordWithRepeatValidator =
            PasswordWithRepeatValidatorImpl(passwordValidator, testScheduler)
        passwordValidationObserver =
            passwordWithRepeatValidator.validatedRepeatPasswordEitherObservable.test()


    }

    @Test
    fun `when no data is provided, then return no validation`() {
        testScheduler.triggerActions()
        passwordValidationObserver.assertValueCount(0)
    }

    @Test
    fun `when original password is invalid, then return no match error`() {
        passwordSubject.onNext(ValidationError.InvalidEmailFormat.left())
        passwordWithRepeatValidator.repeatPasswordChanged(TestData.PASSWORD_VALID_2)

        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.swap().orNull()!! == PasswordError.NotMatching }
    }

    @Test
    fun `when original password is valid, but repeat does not match, then return no match error`() {
        passwordSubject.onNext(TestData.PASSWORD_VALID_1.right())
        passwordWithRepeatValidator.repeatPasswordChanged(TestData.PASSWORD_VALID_2)

        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.swap().orNull()!! == PasswordError.NotMatching }
    }

    @Test
    fun `when the original password and repeat are valid, then return success`() {
        passwordSubject.onNext(TestData.PASSWORD_VALID_1.right())
        passwordWithRepeatValidator.repeatPasswordChanged(TestData.PASSWORD_VALID_1)

        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(1)
            .assertValueAt(0) { it.isRight() }
    }

    @Test
    fun `when invalid original and valid repeat are provided, then correct the original and return success`() {
        passwordSubject.onNext(ValidationError.InvalidEmailFormat.left())
        passwordWithRepeatValidator.repeatPasswordChanged(TestData.PASSWORD_VALID_2)

        testScheduler.triggerActions()

        passwordSubject.onNext(TestData.PASSWORD_VALID_2.right())

        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(2)
            .assertValueAt(0) { it.swap().orNull()!! == PasswordError.NotMatching }
            .assertValueAt(1) { it.isRight() }
    }

    @Test
    fun `when valid original and invalid repeat are provided, then correct repeat and return success`() {
        passwordSubject.onNext(TestData.PASSWORD_VALID_1.right())
        passwordWithRepeatValidator.repeatPasswordChanged(TestData.PASSWORD_WHITE_SPACE)

        testScheduler.triggerActions()

        passwordWithRepeatValidator.repeatPasswordChanged(TestData.PASSWORD_VALID_1)

        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(2)
            .assertValueAt(0) { it.swap().orNull()!! == PasswordError.NotMatching }
            .assertValueAt(1) { it.isRight() }
    }

    @Test
    fun `when both passwords are valid, but different, then correct and return success`() {
        passwordSubject.onNext(TestData.PASSWORD_VALID_1.right())
        passwordWithRepeatValidator.repeatPasswordChanged(TestData.PASSWORD_VALID_2)

        testScheduler.triggerActions()

        passwordWithRepeatValidator.repeatPasswordChanged(TestData.PASSWORD_VALID_1)

        testScheduler.triggerActions()

        passwordValidationObserver.assertValueCount(2)
            .assertValueAt(0) { it.swap().orNull()!! == PasswordError.NotMatching }
            .assertValueAt(1) { it.isRight() }
    }
}