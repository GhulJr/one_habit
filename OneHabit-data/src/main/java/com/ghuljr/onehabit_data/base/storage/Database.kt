package com.ghuljr.onehabit_data.base.storage

import io.objectbox.Box
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler

abstract class BaseDatabase<ENTITY : BaseEntity> {

    protected abstract val userId: String
    protected abstract val box: Box<ENTITY>
    protected abstract val computationScheduler: Scheduler

    fun put(vararg entity: ENTITY) = box.put(*entity)
    fun remove(vararg entity: ENTITY) = box.remove(*entity)
    fun remove(vararg entityId: Long) = box.remove(*entityId)
    fun invalidate() = box.removeAll()
}