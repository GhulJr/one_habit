package com.ghuljr.onehabit_error_android.extension

import android.content.res.Resources
import com.ghuljr.onehabit_error.PasswordError
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_error_android.R

fun ValidationError.textForError(resources: Resources): String = when(this) {
    is ValidationError.EmptyField -> resources.getString(R.string.error_empty_field)
    is ValidationError.InvalidEmailFormat -> resources.getString(R.string.error_invalid_email_format)
    is PasswordError.ToShort -> resources.getString(R.string.error_password_to_short, this.expected)
    is PasswordError.InvalidFormat -> resources.getString(R.string.error_invalid_password_format)
    is PasswordError.NotMatching -> resources.getString(R.string.error_password_do_not_match)
    else -> resources.getString(R.string.error_unknown)
}