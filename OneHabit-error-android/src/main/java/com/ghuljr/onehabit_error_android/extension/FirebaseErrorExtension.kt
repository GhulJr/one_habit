package com.ghuljr.onehabit_error_android.extension

import arrow.core.Either
import arrow.core.left
import com.ghuljr.onehabit_error.*
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseError.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL
import com.google.firebase.FirebaseError.ERROR_EMAIL_ALREADY_IN_USE
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import io.reactivex.rxjava3.core.Single

fun FirebaseAuthException.toError(): BaseError = when(this) {
    is FirebaseAuthEmailException -> AuthError.EmailNotSent(message)
    is FirebaseAuthRecentLoginRequiredException, //TODO: check if this should sign out or make a login deeplink
    is FirebaseAuthInvalidUserException -> LoggedOutError
    is FirebaseAuthMultiFactorException -> AuthError.TwoFactorVerificationFailed(message)
    is FirebaseAuthUserCollisionException -> when(errorCode.toIntOrNull() ?: 0) {
        ERROR_EMAIL_ALREADY_IN_USE -> AuthError.EmailInUse(message)
        ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL -> AuthError.InvalidLoginCredentials(message)
        else -> UnknownError(message ?: "Unknown authorisation error")
    }
    else -> UnknownError(message ?: "Unknown authorisation error")
}

fun FirebaseException.toError(): BaseError = when(this) {
    is FirebaseAuthException -> toError()
    is FirebaseNetworkException -> NetworkError.NoNetwork(message)
    is FirebaseNoSignedInUserException -> LoggedOutError
    is FirebaseApiNotAvailableException -> NetworkError.ServerUnavailable(message)
    is FirebaseTooManyRequestsException -> NetworkError.TooManyRequests(message)
    else -> UnknownError(message ?: "Unknown Firebase error")
}

fun Throwable.toError(): BaseError = when(this) {
    is FirebaseException -> toError()
    else -> UnknownError(message ?: "Unknown error")
}

fun <R> Single<Either<Throwable, R>>.toBaseError(): Single<Either<BaseError, R>> = map { it.mapLeft { it.toError() } }
fun <R> Single<Either<BaseError, R>>.resumeWithBaseError(): Single<Either<BaseError, R>> = onErrorReturn { it.toError().left() }