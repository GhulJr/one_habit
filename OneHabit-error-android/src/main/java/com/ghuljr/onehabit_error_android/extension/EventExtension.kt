package com.ghuljr.onehabit_error_android.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import arrow.core.Either
import com.ghuljr.onehabit_error.*
import com.ghuljr.onehabit_error_android.R

// Text
fun BaseError.textForError(resources: Resources): String = when(this) {
    is NetworkError -> message ?: textForNetworkError(resources)
    is AuthError -> message ?: textForAuthError(resources)
    //TODO: handle remaining errors
    else -> resources.getString(R.string.error_unknown)
}

fun BaseEvent.textForEvent(resources: Resources): String = when(this) {
    is BaseError -> textForError(resources)
    else -> resources.getString(R.string.error_event_unknown)
}

// Drawable
fun BaseError.drawableForError(context: Context): Drawable? = when(this) {
    else -> null
}

fun BaseEvent.drawableForEvent(context: Context): Drawable? = when(this) {
    is BaseError -> drawableForError(context)
    else -> null
}

fun<L, R> Either<L, R>.orLoggedOutError(): Either<BaseError, R> = mapLeft { LoggedOutError }

fun NetworkError.textForNetworkError(resources: Resources): String = when(this) {
    is NetworkError.NoNetwork -> TODO()
    is NetworkError.ServerUnavailable -> TODO()
    is NetworkError.TooManyRequests -> TODO()
}

fun AuthError.textForAuthError(resources: Resources): String = when(this) {
    is AuthError.EmailInUse -> TODO()
    is AuthError.EmailNotSent -> TODO()
    is AuthError.InvalidLoginCredentials -> TODO()
    is AuthError.TwoFactorVerificationFailed -> TODO()
}
