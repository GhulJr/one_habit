package com.ghuljr.onehabit_data.base.storage

import arrow.core.Option
import arrow.core.firstOrNone
import arrow.core.toOption
import io.objectbox.Box
import io.objectbox.rx3.RxQuery
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler


//TODO: in future make an adjustment to better handle ids, because I guess right now it might multiply
// the same data classes, just because objectBoxId is generated anew, or just assign ids by yourself.
//TODO: or maybe it should hold every boxes related to the specific topic? Think how to synchronise such data across
// the queries
//TODO(optional): add the abstraction, so daos would only use interface classes

/**
 * The main idea is to create classes, that exposes pure Kotlin objects (instead of Entities).
 * In every case like this there would be a main Entity which we refer to and relational entities.
 * Every database should represent such
 * */

interface BaseDatabase<ID: Any, ENTITY: BaseEntity<ID>> {
    val dataObservable: Observable<List<ENTITY>>

    fun dataObservable(customId: ID): Observable<List<ENTITY>>
    fun append(vararg entity: ENTITY)
    fun remove(vararg  entity: ENTITY)
    fun replace(vararg entity: ENTITY, insertIfAbsent: Boolean = false, removeIfNotUpdated: Boolean = false)
    fun update(vararg entity: ENTITY)
    fun invalidate()
}

//TODO: use composition over inheritance
internal abstract class BaseDatabaseImpl<ID: Any, ENTITY: BaseEntity<ID>>(
    private val box: Box<ENTITY>,
    private val computationScheduler: ComputationScheduler
) : BaseDatabase<ID, ENTITY> {

    override val dataObservable: Observable<List<ENTITY>> = RxQuery.observable(box.query().build()).subscribeOn(computationScheduler)

    //TODO: create database errors
    //TODO: query this by custom id
    //TODO: generate MyObjectBoxClass
    override fun dataObservable(customId: ID): Observable<List<ENTITY>> = RxQuery.observable(box.query()/*.equal(BaseEntity_)*/.build())
        .subscribeOn(computationScheduler)

/*    abstract fun append(vararg entity: ENTITY)
    abstract fun remove(vararg  entity: ENTITY)
    abstract fun replace(vararg entity: ENTITY, insertIfAbsent: Boolean = false)
    abstract fun update(vararg entity: ENTITY)
    abstract fun invalidate()*/
}