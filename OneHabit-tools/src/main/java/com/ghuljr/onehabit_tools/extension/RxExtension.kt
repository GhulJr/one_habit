package com.ghuljr.onehabit_tools.extension

import arrow.core.*
import com.ghuljr.onehabit_error.BaseEvent
import com.ghuljr.onehabit_error.LoadingEvent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

/** Option */

// toEither
fun <L, R> Flowable<Option<R>>.toEither(onEmptyToLeft: () -> L): Flowable<Either<L, R>> = map {
    it.toEither { onEmptyToLeft() }
}

// switchMap
fun <OLD_R, NEW_R> Flowable<Option<OLD_R>>.switchMapDefined(onDefined: (OLD_R) -> Flowable<NEW_R>): Flowable<Option<NEW_R>> =
    compose { flowable ->
        flowable.switchMap {
            it.fold(
                ifEmpty = { flowable.map { none() } },
                ifSome = { some -> onDefined(some).map { it.some() } }
            )
        }
    }

fun <OLD_R, NEW_R> Flowable<Option<OLD_R>>.switchMapSingleDefined(onDefined: (OLD_R) -> Single<NEW_R>): Flowable<Option<NEW_R>> =
    compose { flowable ->
        flowable.switchMapSingle {
            it.fold(
                ifEmpty = { Single.just(none()) },
                ifSome = { some -> onDefined(some).map { it.some() } }
            )
        }
    }

fun <OLD_R, NEW_R> Flowable<Option<OLD_R>>.switchMapDefinedWithOption(onDefined: (OLD_R) -> Flowable<Option<NEW_R>>): Flowable<Option<NEW_R>> =
    compose { flowable ->
        flowable.switchMap {
            it.fold(
                ifEmpty = { flowable.map { none() } },
                ifSome = { some -> onDefined(some) }
            )
        }
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

fun <L, OLD_R, NEW_R> Flowable<Either<L, OLD_R>>.switchMapMaybeRight(onRight: (OLD_R) -> Maybe<NEW_R>): Flowable<Either<L, NEW_R>> =
    compose { flowable ->
        flowable.switchMapMaybe {
            it.fold(
                { left -> Maybe.just(left.left()) },
                { right -> onRight(right).map { it.right() } })
        }
    }

fun <L, OLD_R, NEW_R> Observable<Either<L, OLD_R>>.switchMapRight(onRight: (OLD_R) -> Observable<NEW_R>): Observable<Either<L, NEW_R>> =
    compose { flowable ->
        flowable.switchMap {
            it.fold(
                { left -> flowable.map { left.left() } },
                { right -> onRight(right).map { it.right() } })
        }
    }

fun <L, OLD_R, NEW_R> Flowable<Either<L, OLD_R>>.switchMapMaybeRightWithEither(onRight: (OLD_R) -> Maybe<Either<L, NEW_R>>): Flowable<Either<L, NEW_R>> =
    compose { flowable ->
        flowable.switchMapMaybe {
            it.fold(
                { left -> Maybe.just(left.left()) },
                { right -> onRight(right) })
        }
    }

fun <L, OLD_R, NEW_R> Observable<Either<L, OLD_R>>.switchMapRightWithEither(onRight: (OLD_R) -> Observable<Either<L, NEW_R>>): Observable<Either<L, NEW_R>> =
    compose { flowable ->
        flowable.switchMap {
            it.fold(
                { left -> Observable.just(left.left()) },
                { right -> onRight(right) })
        }
    }

fun <L, OLD_R, NEW_R> Observable<Either<L, OLD_R>>.switchMapSingleRight(onRight: (OLD_R) -> Single<NEW_R>): Observable<Either<L, NEW_R>> =
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

fun <L, OLD_R, NEW_R> Maybe<Either<L, OLD_R>>.flatMapRight(onRight: (OLD_R) -> Maybe<NEW_R>): Maybe<Either<L, NEW_R>> =
    compose { maybe ->
        maybe.flatMap {
            it.fold(
                { left -> maybe.map { left.left() } },
                { right -> onRight(right).map { it.right() } })
        }
    }

fun <L, OLD_R, NEW_R> Maybe<Either<L, OLD_R>>.flatMapRightWithEither(onRight: (OLD_R) -> Maybe<Either<L, NEW_R>>): Maybe<Either<L, NEW_R>> =
    compose { maybe ->
        maybe.flatMap {
            it.fold(
                { left -> maybe.map { left.left() } },
                { right -> onRight(right) })
        }
    }

// map
fun <L, OLD_R, NEW_R> Single<Either<L, OLD_R>>.mapRight(onRight: (OLD_R) -> NEW_R): Single<Either<L, NEW_R>> = map {
    it.map { right -> onRight(right) }
}

fun <L, OLD_R, NEW_R> Single<Either<L, OLD_R>>.mapRightWithEither(onRight: (OLD_R) -> Either<L, NEW_R>): Single<Either<L, NEW_R>> = map {
    it.flatMap { right -> onRight(right) }
}

fun <L, OLD_R, NEW_R> Maybe<Either<L, OLD_R>>.mapRight(onRight: (OLD_R) -> NEW_R): Maybe<Either<L, NEW_R>> = map {
    it.map { right -> onRight(right) }
}

fun <L, OLD_R, NEW_R> Maybe<Either<L, OLD_R>>.mapRightWithEither(onRight: (OLD_R) -> Either<L, NEW_R>): Maybe<Either<L, NEW_R>> = map {
    it.flatMap { right -> onRight(right) }
}

fun <L, OLD_R, NEW_R> Observable<Either<L, OLD_R>>.mapRight(onRight: (OLD_R) -> NEW_R): Observable<Either<L, NEW_R>> = map {
    it.map { right -> onRight(right) }
}

fun <L, OLD_R, NEW_R> Observable<Either<L, OLD_R>>.mapRightWithEither(onRight: (OLD_R) -> Either<L, NEW_R>): Observable<Either<L, NEW_R>> = map {
    it.flatMap { right -> onRight(right) }
}

fun <L, OLD_R, NEW_R> Flowable<Either<L, OLD_R>>.mapRight(onRight: (OLD_R) -> NEW_R): Flowable<Either<L, NEW_R>> = map {
    it.map { right -> onRight(right) }
}

fun <L, OLD_R, NEW_R> Flowable<Either<L, OLD_R>>.mapRightWithEither(onRight: (OLD_R) -> Either<L, NEW_R>): Flowable<Either<L, NEW_R>> = map {
    it.flatMap { right -> onRight(right) }
}


/////

fun <OLD_L, NEW_L, R> Single<Either<OLD_L, R>>.mapLeft(onLeft: (OLD_L) -> NEW_L): Single<Either<NEW_L, R>> = map {
    it.mapLeft { left -> onLeft(left) }
}

fun <OLD_L, NEW_L, R> Single<Either<OLD_L, R>>.mapLeftLeftWithEither(onLeft: (OLD_L) -> Either<NEW_L, R>): Single<Either<NEW_L, R>> = map {
    it.fold({ left -> onLeft(left)}, { right -> right.right() })
}

fun <OLD_L, NEW_L, R> Maybe<Either<OLD_L, R>>.mapLeft(onLeft: (OLD_L) -> NEW_L): Maybe<Either<NEW_L, R>> = map {
    it.mapLeft { left -> onLeft(left) }
}

fun <OLD_L, NEW_L, R> Maybe<Either<OLD_L, R>>.mapLeftWithEither(onLeft: (OLD_L) -> Either<NEW_L, R>): Maybe<Either<NEW_L, R>> = map {
    it.fold({ left -> onLeft(left)}, { right -> right.right() })
}

fun <OLD_L, NEW_L, R> Observable<Either<OLD_L, R>>.mapLeft(onLeft: (OLD_L) -> NEW_L): Observable<Either<NEW_L, R>> = map {
    it.mapLeft { left -> onLeft(left) }
}

fun <OLD_L, NEW_L, R> Observable<Either<OLD_L, R>>.mapLeftWithEither(onLeft: (OLD_L) -> Either<NEW_L, R>): Observable<Either<NEW_L, R>> = map {
    it.fold({ left -> onLeft(left)}, { right -> right.right() })
}

fun <OLD_L, NEW_L, R> Flowable<Either<OLD_L, R>>.mapLeft(onLeft: (OLD_L) -> NEW_L): Flowable<Either<NEW_L, R>> = map {
    it.mapLeft { left -> onLeft(left) }
}

fun <OLD_L, NEW_L, R> Flowable<Either<OLD_L, R>>.mapLeftWithEither(onLeft: (OLD_L) -> Either<NEW_L, R>): Flowable<Either<NEW_L, R>> = map {
    it.fold({ left -> onLeft(left)}, { right -> right.right() })
}

// toOption
fun <L, R> Flowable<Either<L, R>>.leftToOption(): Flowable<Option<L>> = map { it.swap().orNone() }
fun <L, R> Observable<Either<L, R>>.leftToOption(): Observable<Option<L>> =
    map { it.swap().orNone() }

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
fun <L, R> Flowable<Either<L, R>>.onlyRight(): Flowable<R> =
    filter { it.isRight() }.map { it.orNull()!! }

fun <L, R> Flowable<Either<L, R>>.onlyLeft(): Flowable<L> =
    filter { it.isLeft() }.map { it.swap().orNull()!! }

fun <L, R> Observable<Either<L, R>>.onlyRight(): Observable<R> =
    filter { it.isRight() }.map { it.orNull()!! }

fun <L, R> Observable<Either<L, R>>.onlyLeft(): Observable<L> =
    filter { it.isLeft() }.map { it.swap().orNull()!! }

fun <L, R> Single<Either<L, R>>.onlyRight(): Maybe<R> =
    filter { it.isRight() }.map { it.orNull()!! }

fun <L, R> Single<Either<L, R>>.onlyLeft(): Maybe<L> =
    filter { it.isLeft() }.map { it.swap().orNull()!! }

// Option
fun <V> Flowable<Option<V>>.onlyDefined(): Flowable<V> =
    filter { it.isDefined() }.map { it.orNull()!! }

fun <V> Observable<Option<V>>.onlyDefined(): Observable<V> =
    filter { it.isDefined() }.map { it.orNull()!! }

fun <V> Single<Option<V>>.onlyDefined(): Maybe<V> = filter { it.isDefined() }.map { it.orNull()!! }

/** Errors */
fun <R> Observable<Either<BaseEvent, R>>.startWithLoading(): Observable<Either<BaseEvent, R>> =
    startWithItem(LoadingEvent.left() as Either<BaseEvent, R>)

fun <R> Flowable<Either<BaseEvent, R>>.startWithLoading(): Flowable<Either<BaseEvent, R>> =
    startWithItem(LoadingEvent.left() as Either<BaseEvent, R>)

fun <R> Single<Either<BaseEvent, R>>.toObservableWithLoading(): Observable<Either<BaseEvent, R>> =
    toObservable().startWithLoading()

fun <R> Maybe<Either<BaseEvent, R>>.toObservableWithLoading(): Observable<Either<BaseEvent, R>> =
    toObservable().startWithLoading()

// toBaseEvent
fun <L : BaseEvent, R> Maybe<Either<L, R>>.leftAsEvent(): Maybe<Either<BaseEvent, R>> =
    mapLeft { it as BaseEvent }

fun <L : BaseEvent, R> Single<Either<L, R>>.leftAsEvent(): Single<Either<BaseEvent, R>> =
    mapLeft { it as BaseEvent }