package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.GoalRequest
import com.ghuljr.onehabit_data.network.model.GoalResponse
import com.ghuljr.onehabit_data.network.service.GoalsService
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error_android.extension.leftOnThrow
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.toRx3
import com.ghuljr.onehabit_tools_android.tool.asUnitSingle
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.ashdavies.rx.rxtasks.toSingle
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalsFirebaseService @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler
) : GoalsService {

    private val goalsDb = Firebase.database.getReference("goal")
    private val goalsToMilestonesDb = Firebase.database.getReference("goal_of_milestone")

    override fun getGoalsForMilestone(
        milestoneId: String,
        userId: String
    ): Maybe<Either<BaseError, List<GoalResponse>>> = goalsToMilestonesDb.child(userId)
        .child(milestoneId)
        .get()
        .toSingle()
        .toRx3()
        .map { snapshot -> snapshot.children.map { it.key!! } }
        .toObservable()
        .flatMapIterable { it }
        .flatMapSingle { goalId ->
            goalsDb.child(userId)
                .child(goalId)
                .get()
                .toSingle()
                .toRx3()
                .map {
                    it.getValue(ParsableGoalResponse::class.java)!!
                        .toGoalResponse(userId, goalId, milestoneId)
                }
        }
        .toList()
        .toMaybe()
        .leftOnThrow()
        .observeOn(networkScheduler)

    override fun setGoalsFinished(
        goalIds: List<String>,
        userId: String,
        milestoneId: String
    ): Maybe<Either<BaseError, List<GoalResponse>>> = goalsDb.child(userId)
        .updateChildren(goalIds.map { "/$it/finished" to true }.toMap())
        .asUnitSingle()
        .toObservable()
        .flatMapIterable { goalIds }
        .flatMapSingle { goalId ->
            goalsDb.child(userId)
                .equalTo(goalId)
                .get()
                .toSingle()
                .toRx3()
                .map {
                    it.getValue(ParsableGoalResponse::class.java)!!
                        .toGoalResponse(userId, goalId, milestoneId)
                }
        }
        .toList()
        .toMaybe()
        .leftOnThrow()
        .subscribeOn(networkScheduler)

    override fun putGoals(
        goalRequests: List<GoalRequest>,
        userId: String,
        milestoneId: String
    ): Maybe<Either<BaseError, List<GoalResponse>>> = Observable.fromIterable(goalRequests)
        .flatMapSingle { goalRequest ->
            val key = goalsDb.child(userId).push().key!!
            goalsDb.child(userId)
                .child(key)
                .setValue(ParsableGoalRequest.fromRequest(goalRequest))
                .asUnitSingle()
                .flatMap {
                    goalsToMilestonesDb.child(userId)
                        .child(milestoneId)
                        .child(key)
                        .setValue(true)
                        .asUnitSingle()
                        .flatMap {
                            goalsDb
                                .child(userId)
                                .child(key)
                                .get()
                                .toSingle()
                                .toRx3()
                                .map {
                                    it.getValue(ParsableGoalResponse::class.java)!!.toGoalResponse(
                                        userId = userId,
                                        goalId = key,
                                        milestoneId = milestoneId
                                    )
                                }
                        }
                }
        }
        .toList()
        .toMaybe()
        .leftOnThrow()
        .subscribeOn(networkScheduler)
}

@IgnoreExtraProperties
private data class ParsableGoalResponse(
    @get:PropertyName("remind_at_ms") @set:PropertyName("remind_at_ms") var remindAt: Long? = null,
    @get:PropertyName("day_number") @set:PropertyName("day_number") var dayNumber: Int? = null,
    @get:PropertyName("finished") @set:PropertyName("finished") var finished: Boolean = false
) {
    fun toGoalResponse(userId: String, goalId: String, milestoneId: String) = GoalResponse(
        userId = userId,
        goalId = goalId,
        milestoneId = milestoneId,
        remindAtMs = remindAt,
        dayNumber = dayNumber!!.toLong(),
        finished = finished
    )
}

@IgnoreExtraProperties
private data class ParsableGoalRequest(
    @get:PropertyName("day_number") @set:PropertyName("day_number") var dayNumber: Int? = null,
    @get:PropertyName("finished") @set:PropertyName("finished") var finished: Boolean = false
) {

    companion object {
        fun fromRequest(request: GoalRequest) =
            ParsableGoalRequest(dayNumber = request.dayNumber.toInt())
    }
}