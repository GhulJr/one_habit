package com.ghuljr.onehabit_tools.extension

import arrow.core.*

fun <L,R> Option<R>.toRight(left: L): Either<L, R> = fold({ left.left() }, { it.right() })
fun <L,R> Option<L>.toLeft(right: R): Either<L, R> = fold({ right.right() }, { it.left() })
fun String?.emptyToOption(): Option<String> = if(isNullOrBlank()) none() else some()
fun <T> List<T>?.emptyToOption(): Option<Nel<T>> = Nel.fromList(this ?: emptyList())