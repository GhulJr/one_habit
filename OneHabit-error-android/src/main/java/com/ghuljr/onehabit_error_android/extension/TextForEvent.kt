package com.ghuljr.onehabit_error_android.extension

import android.content.res.Resources
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error_android.R

fun BaseError.textForError(resources: Resources): String = when(this) {
    else -> resources.getString(R.string.error_unknown)
}