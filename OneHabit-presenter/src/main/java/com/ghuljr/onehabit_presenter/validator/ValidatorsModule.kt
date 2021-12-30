package com.ghuljr.onehabit_presenter.validator

import dagger.Binds
import dagger.Module

//TODO: create base input validator
@Module
interface ValidatorsModule {

    @Binds
    fun emailValidator(impl: EmailValidatorImpl): EmailValidator

    @Binds
    fun passwordValidator(impl: PasswordValidatorImpl): PasswordValidator

    @Binds
    fun passwordWithRepeatValidator(impl: PasswordWithRepeatValidatorImpl): PasswordWithRepeatValidator

    @Binds
    fun fieldValidator(impl: FieldValidatorImpl): FieldValidator
}