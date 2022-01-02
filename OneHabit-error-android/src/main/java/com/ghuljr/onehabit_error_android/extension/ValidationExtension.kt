package com.ghuljr.onehabit_error_android.extension

import android.content.res.Resources
import arrow.core.Option
import com.ghuljr.onehabit_error.PasswordError
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_error_android.R
import com.google.android.material.textfield.TextInputLayout

fun ValidationError.textForError(resources: Resources): String = when(this) {
    is ValidationError.EmptyField -> resources.getString(R.string.error_empty_field)
    is ValidationError.InvalidEmailFormat -> resources.getString(R.string.error_invalid_email_format)
    is PasswordError.ToShort -> resources.getString(R.string.error_password_to_short, this.expected)
    is PasswordError.InvalidFormat -> resources.getString(R.string.error_invalid_password_format)
    is PasswordError.NotMatching -> resources.getString(R.string.error_password_do_not_match)
    else -> resources.getString(R.string.error_unknown)
}


fun <E : ValidationError> TextInputLayout.setErrorOption(error: Option<E>) {
    this.error = error.orNull()?.textForError(resources)
}