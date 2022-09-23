package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.ActionResponse
import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_data.repository.LoggedInUserRepository
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_error_android.extension.leftOnThrow
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.flatMapRightWithEither
import com.ghuljr.onehabit_tools.extension.toEither
import com.ghuljr.onehabit_tools.extension.toRx3
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.ashdavies.rx.rxtasks.toSingle
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionsFirebaseService @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler
) : ActionsService {

    private val actionDb = Firebase.database.getReference("action")
    private val actionToGoalDb = Firebase.database.getReference("action_of_goal")

    override fun getActionsFromGoal(
        goalId: String,
        userId: String
    ): Maybe<Either<BaseError, List<ActionResponse>>> = getTodayActionsIds(userId, goalId)
            .toObservable()
            .flatMapIterable { it }
            .flatMapSingle { actionId ->
                actionDb.child(userId).child(actionId).get()
                    .toSingle()
                    .toRx3()
                    .map {
                        it.getValue(ParsableActionResponse::class.java)!!
                            .toActionResponse(it.key!!)
                    }
            }
            .toList()
            .leftOnThrow()
            .toMaybe()
            .subscribeOn(networkScheduler)

    private fun getTodayActionsIds(userId: String, goalId: String): Single<List<String>> =
        actionToGoalDb.child(userId)
            .child(goalId)
            .get()
            .toSingle()
            .toRx3()
            .map { snapshot -> snapshot.children.map { it.key!! } }

}

@IgnoreExtraProperties
private data class ParsableActionResponse(
    @get:PropertyName("remind_at_ms") @set:PropertyName("remind_at_ms") var remindersAtMs: List<Long>? = null,
    @get:PropertyName("repeats_current") @set:PropertyName("repeats_current") var currentRepeat: Int? = null,
    @get:PropertyName("repeats_max") @set:PropertyName("repeats_max") var totalRepeats: Int? = null,
    @get:PropertyName("custom") @set:PropertyName("custom") var custom: Boolean = false
) {

    fun toActionResponse(id: String) = ActionResponse(
        id = id,
        remindersAtMs = remindersAtMs,
        currentRepeat = currentRepeat!!,
        totalRepeats = totalRepeats!!,
        custom = custom
    )
}