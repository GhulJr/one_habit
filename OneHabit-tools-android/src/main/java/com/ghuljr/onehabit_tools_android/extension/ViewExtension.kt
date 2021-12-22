package com.ghuljr.onehabit_tools_android.extension

import android.content.Intent


fun Intent.asSingleTop(): Intent = apply {
    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
}