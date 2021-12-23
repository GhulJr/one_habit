package com.ghuljr.onehabit_error

sealed class ValidationError: BaseError {
    object EmptyField : ValidationError()
    object InvalidEmailFormat : ValidationError()
}

sealed class PasswordError : ValidationError() {
    object InvalidFormat : PasswordError()
    object NotMatching : PasswordError()
    data class ToShort(val provided: Int, val expected: Int) : PasswordError()
}