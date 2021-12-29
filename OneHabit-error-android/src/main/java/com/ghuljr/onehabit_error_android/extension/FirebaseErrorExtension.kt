package com.ghuljr.onehabit_error_android.extension

import android.util.Log
import arrow.core.Either
import arrow.core.left
import com.ghuljr.onehabit_error.*
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import io.reactivex.rxjava3.core.Single

fun FirebaseAuthException.toError(): BaseError = when(this) {
    is FirebaseAuthEmailException -> AuthError.EmailNotSent(message)
    is FirebaseAuthRecentLoginRequiredException, //TODO: check if this should sign out or make a login deeplink
    is FirebaseAuthInvalidUserException -> when(errorCode) {
        "ERROR_USER_DISABLED", "ERROR_USER_NOT_FOUND" -> AuthError.AccountDoNotExist(message)
        else -> LoggedOutError
    }
    is FirebaseAuthMultiFactorException -> AuthError.TwoFactorVerificationFailed(message)
    is FirebaseAuthUserCollisionException -> when(errorCode) {
        "ERROR_EMAIL_ALREADY_IN_USE" -> AuthError.EmailInUse(message)
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> AuthError.InvalidLoginCredentials(message)
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

// TODO: as long as I use Firebase such extensions must be placed in android module
fun <R> Single<Either<Throwable, R>>.toBaseError(): Single<Either<BaseError, R>> = map { it.mapLeft { it.toError() } }
fun <R> Single<Either<BaseError, R>>.resumeWithBaseError(): Single<Either<BaseError, R>> = onErrorReturn {
    Log.e("Handled exception", "", it)
    it.toError().left()
}