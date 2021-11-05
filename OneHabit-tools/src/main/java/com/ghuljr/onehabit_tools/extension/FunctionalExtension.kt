package com.ghuljr.onehabit_tools.extension

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.right

fun <L,R> Option<R>.toRight(left: L): Either<L, R> = fold({ left.left() }, { it.right() })
fun <L,R> Option<L>.toLeft(right: R): Either<L, R> = fold({ right.right() }, { it.left() })