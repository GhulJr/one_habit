package com.ghuljr.onehabit_data.storage.persistence

import com.ghuljr.onehabit_data.base.storage.BaseDatabase
import com.ghuljr.onehabit_data.storage.model.ActionEntity
import com.ghuljr.onehabit_data.storage.model.ActionEntity_
import io.objectbox.Box
import io.objectbox.query.QueryBuilder
import io.objectbox.rx3.RxQuery
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler


class ActionDatabase(
    override val box: Box<ActionEntity>, // TODO: possibly will need @SurpressWildcharts or something like this
    override val computationScheduler: Scheduler,
    private val userId: String
) : BaseDatabase<ActionEntity>() {

    override val dataObservable: Observable<List<ActionEntity>> = RxQuery.observable(
        box.query()
            .equal(ActionEntity_.userId, userId, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
    )
}