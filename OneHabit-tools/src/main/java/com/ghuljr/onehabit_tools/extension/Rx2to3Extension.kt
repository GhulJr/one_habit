package com.ghuljr.onehabit_tools.extension

import hu.akarnokd.rxjava3.bridge.RxJavaBridge

fun <T> io.reactivex.Single<T>.toRx3(): io.reactivex.rxjava3.core.Single<T> = RxJavaBridge.toV3Single(this)