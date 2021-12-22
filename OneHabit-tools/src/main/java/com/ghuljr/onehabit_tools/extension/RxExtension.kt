package com.ghuljr.onehabit_tools.extension

import arrow.core.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
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

// toOption
fun <L, R> Flowable<Either<L, R>>.leftToOption(): Flowable<Option<L>> = map { it.swap().orNone() }
fun <L, R> Observable<Either<L, R>>.leftToOption(): Observable<Option<L>> = map { it.swap().orNone() }
fun <L, R> Single<Either<L, R>>.leftToOption(): Single<Option<L>> = map { it.swap().orNone() }
fun <L, R> Maybe<Either<L, R>>.leftToOption(): Maybe<Option<L>> = map { it.swap().orNone() }


/** Other */

fun <V> Flowable<V>.toUnit(): Flowable<Unit> = map { Unit }
fun <V> Observable<V>.toUnit(): Observable<Unit> = map { Unit }
fun <V> Single<V>.toUnit(): Single<Unit> = map { Unit }
fun <V> Maybe<V>.toUnit(): Maybe<Unit> = map { Unit }

/** Filter */

// Boolean
fun Flowable<Boolean>.onlyTrue(): Flowable<Unit> = filter { it }.toUnit()
fun Flowable<Boolean>.onlyFalse(): Flowable<Unit> = filter { !it }.toUnit()
fun Observable<Boolean>.onlyTrue(): Observable<Unit> = filter { it }.toUnit()
fun Observable<Boolean>.onlyFalse(): Observable<Unit> = filter { !it }.toUnit()
fun Single<Boolean>.onlyTrue(): Maybe<Unit> = filter { it }.toUnit()
fun Single<Boolean>.onlyFalse(): Maybe<Unit> = filter { !it }.toUnit()

// Either
fun<L, R> Flowable<Either<L, R>>.onlyRight(): Flowable<R> = filter { it.isRight() }.map { it.orNull()!! }
fun<L, R> Flowable<Either<L, R>>.onlyLeft(): Flowable<L> = filter { it.isLeft() }.map { it.swap().orNull()!! }
fun<L, R> Observable<Either<L, R>>.onlyRight(): Observable<R> = filter { it.isRight() }.map { it.orNull()!! }
fun<L, R> Observable<Either<L, R>>.onlyLeft(): Observable<L> = filter { it.isLeft() }.map { it.swap().orNull()!! }
fun<L, R> Single<Either<L, R>>.onlyRight(): Maybe<R> = filter { it.isRight() }.map { it.orNull()!! }
fun<L, R> Single<Either<L, R>>.onlyLeft(): Maybe<L> = filter { it.isLeft() }.map { it.swap().orNull()!! }
