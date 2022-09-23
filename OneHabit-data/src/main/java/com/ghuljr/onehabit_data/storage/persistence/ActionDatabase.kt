package com.ghuljr.onehabit_data.storage.persistence

import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.storage.model.ActionEntity
import com.ghuljr.onehabit_data.storage.model.ActionEntity_
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.objectbox.Box
import io.objectbox.query.QueryBuilder
import io.objectbox.rx3.RxQuery
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject


class ActionDatabase @AssistedInject constructor(
    override val box: Box<ActionEntity>,
    override val computationScheduler: Scheduler,
    @Assisted private val userId: String
) : BaseDatabase<ActionEntity>() {

    override val dataObservable: Observable<List<ActionEntity>> = RxQuery.observable(
        box.query()
            .equal(ActionEntity_.userId, userId, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
    )

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ActionDatabase
    }
}