package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.reduceOrNull
import arrow.core.right
import com.ghuljr.onehabit_data.network.model.*
import com.ghuljr.onehabit_data.network.service.ActionsService
import com.ghuljr.onehabit_data.network.service.GoalsService
import com.ghuljr.onehabit_data.network.service.MilestoneService
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error_android.extension.leftOnThrow
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.flatMapMaybeRightWithEither
import com.ghuljr.onehabit_tools.extension.flatMapRightWithEither
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.toRx3
import com.ghuljr.onehabit_tools_android.tool.asUnitSingle
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.ashdavies.rx.rxtasks.toSingle
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilestoneFirebaseService @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler,
    private val actionService: ActionsService,
    private val goalService: GoalsService
) : MilestoneService {

    private val milestoneDatabase = Firebase.database.getReference("milestone")
    private val cacheDatabase = Firebase.database.getReference("milestone_of_habit")

    override fun getMilestoneById(
        userId: String,
        milestoneId: String
    ): Maybe<Either<BaseError, MilestoneResponse>> = milestoneDatabase
        .child(userId)
        .child(milestoneId)
        .get()
        .toSingle()
        .toRx3()
        .map {
            it.getValue(ParsableMilestoneResponse::class.java)!!.toResponse(milestoneId, userId)
        }
        .toMaybe()
        .leftOnThrow()
        .subscribeOn(networkScheduler)

    override fun getMilestonesByHabitId(
        userId: String,
        habitId: String
    ): Maybe<Either<BaseError, List<MilestoneResponse>>> = cacheDatabase
        .child(userId)
        .child(habitId)
        .get()
        .toSingle()
        .toRx3()
        .toObservable()
        .flatMapIterable { it.children.map { it.key!! } }
        .leftOnThrow()
        .flatMapMaybeRightWithEither { milestoneId -> getMilestoneById(userId = userId, milestoneId = milestoneId) }
        .toList()
        .toMaybe()
        .map {
            it.reduceOrNull(
                initial = { it.map { listOf(it) } },
                operation = { acc, new -> acc.flatMap { list -> new.map { list + it } } }
            ) ?: listOf<MilestoneResponse>().right()
        }
        .subscribeOn(networkScheduler)

    override fun resolveMilestone(
        userId: String,
        milestoneId: String
    ): Maybe<Either<BaseError, MilestoneResponse>> = milestoneDatabase
        .child(userId)
        .child(milestoneId)
        .updateChildren(mapOf("resolved" to true))
        .asUnitSingle()
        .leftOnThrow()
        .toMaybe()
        .flatMapRightWithEither { getMilestoneById(userId, milestoneId) }
        .subscribeOn(networkScheduler)

    override fun generateMilestone(
        milestoneRequest: MilestoneRequest,
        goalRequests: List<GoalRequest>,
        actionRequest: ActionRequest,
        frequency: Int
    ): Maybe<Either<BaseError, MilestoneResponse>> {
        val key = milestoneDatabase.child(milestoneRequest.userId).push().key!!
        return milestoneDatabase
            .child(milestoneRequest.userId)
            .child(key)
            .setValue(ParsableMilestoneRequest(intensity = milestoneRequest.intensity))
            .asUnitSingle()
            .flatMap {
                cacheDatabase.child(milestoneRequest.userId)
                    .child(milestoneRequest.habitId)
                    .child(key)
                    .setValue(true)
                    .asUnitSingle()
            }
            .toMaybe()
            .leftOnThrow()
            .flatMapRightWithEither {
                goalService.putGoals(
                    goalRequests = goalRequests,
                    userId = milestoneRequest.userId,
                    milestoneId = key
                ).flatMapRightWithEither { goals ->
                    if (frequency == 0) {
                        actionService.putOneActionToManyGoals(actionRequest, goals.map { it.goalId })
                            .mapRight { listOf(it) }
                    } else actionService.putActions(goals.map { it.goalId to actionRequest })
                }
            }
            .flatMapRightWithEither { getMilestoneById(milestoneRequest.userId, key) }
            .subscribeOn(networkScheduler)
    }


}

@IgnoreExtraProperties
private data class ParsableMilestoneResponse(
    @get:PropertyName("intensity") @set:PropertyName("intensity") var intensity: Int = 0,
    @get:PropertyName("resolved_at") @set:PropertyName("resolved_at") var resolvedAt: Long? = null
) {
    fun toResponse(id: String, userId: String) = MilestoneResponse(
        id = id,
        userId = userId,
        intensity = intensity,
        resolvedAt = resolvedAt
    )
}

@IgnoreExtraProperties
private data class ParsableMilestoneRequest(@get:PropertyName("intensity") @set:PropertyName("intensity") var intensity: Int = 0)