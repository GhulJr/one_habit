package com.ghuljr.onehabit_presenter.validator

import dagger.Binds
import dagger.Module

@Module
interface ValidatorsModule {

    @Binds
    fun emailValidator(impl: EmailValidatorImpl): EmailValidator

    @Binds
    fun passwordValidator(impl: PasswordValidatorImpl): PasswordValidator

    @Binds
    fun passwordWithRepeatValidator(impl: PasswordWithRepeatValidatorImpl): PasswordWithRepeatValidator
}