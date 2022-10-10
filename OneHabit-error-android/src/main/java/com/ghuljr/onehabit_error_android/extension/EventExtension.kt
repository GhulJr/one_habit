package com.ghuljr.onehabit_error_android.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.ghuljr.onehabit_error.*
import com.ghuljr.onehabit_error_android.R

// Text
fun BaseError.textForError(resources: Resources): String = when (this) {
    is NetworkError -> message ?: textForNetworkError(resources)
    is AuthError -> message ?: textForAuthError(resources)
    is LoggedOutError -> resources.getString(R.string.error_logged_out_error)
    is NoDataError -> resources.getString(R.string.error_no_data)
    is GenericError -> cause.message ?: resources.getString(R.string.error_unknown_param, cause.toString())
    is ValidationError -> this.textForError(resources)
    else -> resources.getString(R.string.error_unknown)
}

fun BaseEvent.textForEvent(resources: Resources): String = when (this) {
    is BaseError -> textForError(resources)
    else -> resources.getString(R.string.error_event_unknown)
}

// Drawable
fun BaseError.drawableForError(context: Context): Drawable? = when (this) {
    else -> null
}

fun BaseEvent.drawableForEvent(context: Context): Drawable? = when (this) {
    is BaseError -> drawableForError(context)
    else -> null
}

fun NetworkError.textForNetworkError(resources: Resources): String = when (this) {
    is NetworkError.NoNetwork -> resources.getString(R.string.error_no_network)
    is NetworkError.ServerUnavailable -> resources.getString(R.string.error_server_unavailable)
    is NetworkError.TooManyRequests -> resources.getString(R.string.error_too_many_requests)
}

fun AuthError.textForAuthError(resources: Resources): String = when (this) {
    is AuthError.EmailNotYetVerified -> resources.getString(R.string.error_email_not_verified_yet)
    is AuthError.EmailInUse -> resources.getString(R.string.error_email_in_use)
    is AuthError.EmailNotSent -> resources.getString(R.string.error_email_not_sent)
    is AuthError.InvalidLoginCredentials -> resources.getString(R.string.error_email_in_use)
    is AuthError.TwoFactorVerificationFailed -> resources.getString(R.string.error_two_factor_failed)
    is AuthError.AccountDoNotExist -> resources.getString(R.string.error_account_do_not_exist)
    is AuthError.RequireReAuthentication -> resources.getString(R.string.error_reauthenticate)
}
