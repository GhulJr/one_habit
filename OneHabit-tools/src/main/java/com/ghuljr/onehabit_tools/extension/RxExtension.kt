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

fun <L, OLD_R, NEW_R> Flowable<Either<L, OLD_R>>.switchMapRightWithEither(onRight: (OLD_R) -> Flowable<Either<L, NEW_R>>): Flowable<Either<L, NEW_R>> =
    compose { flowable ->
        flowable.switchMap {
            it.fold(
                { left -> flowable.map { left.left() } },
                { right -> onRight(right) })
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

// flatMap
fun <L, OLD_R, NEW_R> Single<Either<L, OLD_R>>.flatMapRight(onRight: (OLD_R) -> Single<NEW_R>): Single<Either<L, NEW_R>> =
    compose { single ->
        single.flatMap {
            it.fold(
                { left -> single.map { left.left() } },
                { right -> onRight(right).map { it.right() } })
        }
    }

fun <L, OLD_R, NEW_R> Single<Either<L, OLD_R>>.flatMapRightWithEither(onRight: (OLD_R) -> Single<Either<L, NEW_R>>): Single<Either<L, NEW_R>> =
    compose { single ->
        single.flatMap {
            it.fold(
                { left -> single.map { left.left() } },
                { right -> onRight(right) })
        }
    }


/** Other */
fun <V> Flowable<V>.toUnit(): Flowable<Unit> = map { Unit }