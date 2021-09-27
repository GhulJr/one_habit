package com.ghuljr.onehabit_tools.extension

import arrow.core.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

/** Option */

// toEither
fun <L, R> Flowable<Option<R>>.toEither(onEmptyToLeft: () -> L): Flowable<Either<L, R>> = map {
    it.toEither { onEmptyToLeft() }
}

/** Either */

// switchMap
fun <L, OLD_R, NEW_R> Flowable<Either<L, OLD_R>>.switchMapRight(onRight: (OLD_R) -> Flowable<NEW_R>): Flowable<Either<L, NEW_R>> =
    compose { flowable ->
        flowable.switchMap {
            it.fold(
                { left -> flowable.map { left.left() } },
                { right -> onRight(right).map { it.right() } })
        }
    }

fun <L, OLD_R, NEW_R> Flowable<Either<L, OLD_R>>.switchMapSingleRight(onRight: (OLD_R) -> Single<NEW_R>): Flowable<Either<L, NEW_R>> =
    compose { flowable ->
        flowable.switchMapSingle {
            it.fold(
                { left -> Single.just(left.left()) },
                { right -> onRight(right).map { it.right() } })
        }
    }