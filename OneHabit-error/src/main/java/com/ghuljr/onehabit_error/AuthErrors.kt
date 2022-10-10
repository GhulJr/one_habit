package com.ghuljr.onehabit_error

sealed class AuthError(open val message: String?): BaseError {
    object EmailNotYetVerified : AuthError(null)
    object RequireReAuthentication : AuthError(null)
    data class EmailNotSent(override val message: String?): AuthError(message)
    data class TwoFactorVerificationFailed(override val message: String?): AuthError(message)
    data class EmailInUse(override val message: String?): AuthError(message)
    data class AccountDoNotExist(override val message: String?): AuthError(message)
    data class InvalidLoginCredentials(override val message: String?): AuthError(message)
}