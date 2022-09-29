package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.ActionRequest
import com.ghuljr.onehabit_data.network.model.ActionResponse
import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error_android.extension.leftOnThrow
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.flatMapRightWithEither
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.toRx3
import com.ghuljr.onehabit_tools_android.tool.asUnitSingle
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.ashdavies.rx.rxtasks.toSingle
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
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


    override fun completeActionStep(
        actionId: String,
        userId: String
    ): Maybe<Either<BaseError, ActionResponse>> = actionDb
        .child(userId)
        .child(actionId)
        .updateChildren(hashMapOf("repeats_current" to ServerValue.increment(1)))
        .asUnitSingle()
        .toMaybe()
        .leftOnThrow()
        .flatMapRightWithEither { getActionById(actionId, userId) }
        .subscribeOn(networkScheduler)


    override fun revertCompleteActionStep(
        actionId: String,
        userId: String
    ): Maybe<Either<BaseError, ActionResponse>> = actionDb
        .child(userId)
        .child(actionId)
        .updateChildren(hashMapOf("repeats_current" to ServerValue.increment(-1)))
        .asUnitSingle()
        .toMaybe()
        .leftOnThrow()
        .flatMapRightWithEither { getActionById(actionId, userId) }
        .subscribeOn(networkScheduler)

    override fun putAction(
        actionRequest: ActionRequest
    ): Maybe<Either<BaseError, ActionResponse>> {
        val actionsReference = actionDb.child(actionRequest.userId)
        val key = actionsReference.push().key!!

        return actionsReference.child(key)
            .setValue(ParsableActionRequest.fromActionRequest(actionRequest, key))
            .asUnitSingle()
            .flatMap {
                actionToGoalDb.child(actionRequest.userId)
                    .child(actionRequest.goalId)
                    .child(key)
                    .setValue(true)
                    .asUnitSingle()
            }
            .toMaybe()
            .leftOnThrow()
            .mapRight { actionRequest.toActionResponse(key) }
            .subscribeOn(networkScheduler)
    }

    override fun addRemindersToAction(
        userId: String,
        actionId: String,
        reminders: List<Long>
    ): Maybe<Either<BaseError, ActionResponse>> = actionDb.child(userId)
        .child(actionId)
        .updateChildren(mapOf("remindes" to reminders))
        .asUnitSingle()
        .toMaybe()
        .leftOnThrow()
        .flatMapRightWithEither { getActionById(actionId, userId) }
        .subscribeOn(networkScheduler)

    override fun editCustomAction(
        actionName: String,
        userId: String,
        actionId: String,
        reminders: List<Long>
    ): Maybe<Either<BaseError, ActionResponse>> = actionDb.child(userId)
        .child(actionId)
        .updateChildren(
            mapOf(
                "custom_title" to actionName,
                "reminders" to reminders
            )
        )
        .asUnitSingle()
        .toMaybe()
        .leftOnThrow()
        .flatMapRightWithEither { getActionById(actionId, userId) }
        .subscribeOn(networkScheduler)

    override fun removeAction(
        actionId: String,
        userId: String,
        goalId: String
    ): Maybe<Either<BaseError, Unit>> = actionDb.child(userId)
        .child(actionId)
        .removeValue()
        .asUnitSingle()
        .flatMap {
            actionToGoalDb.child(userId)
                .child(goalId)
                .child(actionId)
                .removeValue()
                .asUnitSingle()
        }
        .toMaybe()
        .leftOnThrow()
        .subscribeOn(networkScheduler)

    private fun getTodayActionsIds(userId: String, goalId: String): Single<List<String>> =
        actionToGoalDb.child(userId)
            .child(goalId)
            .get()
            .toSingle()
            .toRx3()
            .map { snapshot -> snapshot.children.map { it.key!! } }

    private fun getActionById(
        actionId: String,
        userId: String
    ): Maybe<Either<BaseError, ActionResponse>> = actionDb
        .child(userId)
        .child(actionId)
        .get()
        .toSingle()
        .toRx3()
        .toMaybe()
        .map { it.getValue(ParsableActionResponse::class.java)!!.toActionResponse(it.key!!) }
        .leftOnThrow()
        .subscribeOn(networkScheduler)

}

@IgnoreExtraProperties
private data class ParsableActionResponse(
    @get:PropertyName("remind_at_ms") @set:PropertyName("remind_at_ms") var remindersAtMs: List<Long>? = null,
    @get:PropertyName("repeats_current") @set:PropertyName("repeats_current") var currentRepeat: Int? = null,
    @get:PropertyName("repeats_max") @set:PropertyName("repeats_max") var totalRepeats: Int? = null,
    @get:PropertyName("custom_title") @set:PropertyName("custom_title") var customTitle: String? = null
) {

    fun toActionResponse(id: String) = ActionResponse(
        id = id,
        remindersAtMs = remindersAtMs,
        currentRepeat = currentRepeat!!,
        totalRepeats = totalRepeats!!,
        customTitle = customTitle
    )
}

@IgnoreExtraProperties
private data class ParsableActionRequest(
    @get:PropertyName("remind_at_ms") @set:PropertyName("remind_at_ms") var remindersAtMs: List<Long>? = null,
    @get:PropertyName("repeats_current") @set:PropertyName("repeats_current") var currentRepeat: Int? = null,
    @get:PropertyName("repeats_max") @set:PropertyName("repeats_max") var totalRepeats: Int? = null,
    @get:PropertyName("custom_title") @set:PropertyName("custom_title") var customTitle: String? = null
) {

    companion object {

        fun fromActionRequest(actionRequest: ActionRequest, id: String) = actionRequest.run {
            ParsableActionRequest(
                remindersAtMs = remindersAtMs,
                currentRepeat = currentRepeat,
                totalRepeats = totalRepeats,
                customTitle = customTitle
            )
        }
    }
}