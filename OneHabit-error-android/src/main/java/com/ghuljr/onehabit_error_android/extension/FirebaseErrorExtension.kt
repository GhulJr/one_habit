package com.ghuljr.onehabit_error_android.extension

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ghuljr.onehabit_error.*
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

fun FirebaseAuthException.toError(): BaseError = when (this) {
    is FirebaseAuthEmailException -> AuthError.EmailNotSent(message)
    is FirebaseAuthRecentLoginRequiredException, //TODO: check if this should sign out or make a login deeplink
    is FirebaseAuthInvalidUserException -> when (errorCode) {
        "ERROR_USER_DISABLED", "ERROR_USER_NOT_FOUND" -> AuthError.AccountDoNotExist(message)
        "ERROR_REQUIRES_RECENT_LOGIN" -> AuthError.RequireReAuthentication
        else -> LoggedOutError
    }
    is FirebaseAuthMultiFactorException -> AuthError.TwoFactorVerificationFailed(message)
    is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidLoginCredentials(message)
    is FirebaseAuthUserCollisionException -> when (errorCode) {
        "ERROR_EMAIL_ALREADY_IN_USE" -> AuthError.EmailInUse(message)
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> AuthError.InvalidLoginCredentials(message)
        else -> GenericError(this)
    }
    else -> GenericError(this)
}

fun FirebaseException.toError(): BaseError = when (this) {
    is FirebaseAuthException -> toError()
    is FirebaseNetworkException -> NetworkError.NoNetwork(message)
    is FirebaseNoSignedInUserException -> LoggedOutError
    is FirebaseApiNotAvailableException -> NetworkError.ServerUnavailable(message)
    is FirebaseTooManyRequestsException -> NetworkError.TooManyRequests(message)
    else -> GenericError(this)
}

fun Throwable.toError(): BaseError = when (this) {
    is FirebaseException -> toError()
    else -> GenericError(this)
}

fun <L, R> Either<L, R>.orLoggedOutError(): Either<BaseError, R> = mapLeft { LoggedOutError }

fun <R: Any> Single<R>.leftOnThrow(): Single<Either<BaseError, R>> = map { it.right() as Either<BaseError, R> }
        .onErrorReturn {
            Log.e("Handled exception", "", it)
            it.toError().left()
        }

fun <R: Any> Maybe<R>.leftOnThrow(): Maybe<Either<BaseError, R>> = map { it.right() as Either<BaseError, R> }
    .onErrorReturn {
        Log.e("Handled exception", "", it)
        it.toError().left()
    }

fun <R: Any> Flowable<R>.leftOnThrow(): Flowable<Either<BaseError, R>> = map { it.right() as Either<BaseError, R> }
    .onErrorReturn {
        Log.e("Handled exception", "", it)
        it.toError().left()
    }

fun <R: Any> Observable<R>.leftOnThrow(): Observable<Either<BaseError, R>> = map { it.right() as Either<BaseError, R> }
    .onErrorReturn {
        Log.e("Handled exception", "", it)
        it.toError().left()
    }

fun <R> Single<Either<BaseError, R>>.resumeWithBaseError(): Single<Either<BaseError, R>> = onErrorReturn {
    Log.e("Handled exception", "", it)
    it.toError().left()
}