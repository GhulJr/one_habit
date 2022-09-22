package com.ghuljr.onehabit_data.base.storage

import io.objectbox.Box
import io.objectbox.rx3.RxQuery
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler

abstract class BaseDatabase<ENTITY : BaseEntity> {

    protected abstract val box: Box<ENTITY>
    protected abstract val computationScheduler: Scheduler

    abstract val dataObservable: Observable<List<ENTITY>>

    fun put(vararg entity: ENTITY) = box.put(*entity)
    fun remove(vararg entity: ENTITY) = box.remove(*entity)
    fun remove(vararg entityId: Long) = box.remove(*entityId)
    fun invalidate() = box.removeAll()
}