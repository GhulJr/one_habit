package com.ghuljr.onehabit_error_android.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.NoDataError
import com.ghuljr.onehabit_error_android.R

// Text
fun BaseError.textForError(resources: Resources): String = when(this) {
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