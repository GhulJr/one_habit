package com.ghuljr.onehabit_data.storage.persistence

import arrow.core.firstOrNone
import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.storage.model.UserEntity
import com.ghuljr.onehabit_data.storage.model.UserEntityHolder
import com.ghuljr.onehabit_data.storage.model.UserEntityHolder_
import com.ghuljr.onehabit_data.storage.model.UserEntity_
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import dagger.assisted.AssistedInject
import io.objectbox.Box
import io.objectbox.query.QueryBuilder
import io.objectbox.rx3.RxQuery
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class UserMetadataDatabase @Inject constructor(
    override val box: Box<UserEntity>,
    private val cacheBox: Box<UserEntityHolder>,
    @ComputationScheduler override val computationScheduler: Scheduler
) : BaseDatabase<UserEntity>() {

    override val userId: String = "" // Not required

    fun userMetadata(userId: String): Flowable<DataSource.CacheWithTime<UserEntity>> =
        RxQuery.observable(
            box.query().equal(UserEntity_.userId, userId, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
        ).map {
            DataSource.CacheWithTime(
                value = it.firstOrNone(),
                dueToInMillis = cacheBox.query()
                    .equal(UserEntityHolder_.userId, userId, QueryBuilder.StringOrder.CASE_SENSITIVE)
                    .build()
                    .findUnique()
                    ?.dueToInMillis ?: 0L
            )
        }
            .toFlowable(BackpressureStrategy.BUFFER)
            .subscribeOn(computationScheduler)

    fun removeUser(userId: String) {
        box.query()
            .equal(UserEntity_.userId, userId, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
            .remove()
    }

    fun replaceUser(userId: String, userEntity: UserEntity?, dueToMs: Long) {
        if (userEntity == null)
            removeUser(userId)
        else {
            put()
            cacheBox.put(
                UserEntityHolder(
                    userId = userId,
                    dueToInMillis = dueToMs
                )
            )
        }
    }
}