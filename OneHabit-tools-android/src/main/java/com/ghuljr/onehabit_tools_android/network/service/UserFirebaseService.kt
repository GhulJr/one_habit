package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.*
import com.ghuljr.onehabit_data.network.model.UserMetadataResponse
import com.ghuljr.onehabit_data.network.service.UserService
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error_android.extension.leftOnThrow
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.flatMapRightWithEither
import com.ghuljr.onehabit_tools.extension.toRx3
import com.ghuljr.onehabit_tools_android.tool.asUnitSingle
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.ashdavies.rx.rxtasks.toSingle
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserFirebaseService @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler
) : UserService {

    private val userDatabase = Firebase.database.getReference("user")
    private val milestoneDatabase = Firebase.database.getReference("milestone")
    private val milestoneOfHabitDatabase = Firebase.database.getReference("milestone_of_habit")
    private val topTierHabitsDatabase = Firebase.database.getReference("top_tier_habits")

    override fun getUserMetadata(userId: String): Maybe<Either<BaseError, UserMetadataResponse>> =
        userDatabase.child(userId)
            .get()
            .toSingle()
            .toRx3()
            .toMaybe()
            .zipWith(getTopTierHabits(userId)) { userSnapshot, topTierHabitsIds ->  userSnapshot to topTierHabitsIds }
            .map { (snapshot, topTierHabitIds) ->
                snapshot.getValue(ParsableUserMetadataResponse::class.java)!!
                    .toUserMetadataResponse(userId, topTierHabitIds)
            }
            .leftOnThrow()
            .subscribeOn(networkScheduler)

    override fun setCurrentGoal(
        userId: String,
        goalId: String
    ): Maybe<Either<BaseError, UserMetadataResponse>> = userDatabase.child(userId)
        .updateChildren(mapOf("goal" to goalId))
        .asUnitSingle()
        .leftOnThrow()
        .toMaybe()
        .flatMapRightWithEither { getUserMetadata(userId) }
        .subscribeOn(networkScheduler)

    override fun setCurrentMilestone(
        userId: String,
        previousMilestoneId: String?,
        milestoneId: String
    ): Maybe<Either<BaseError, UserMetadataResponse>> = userDatabase.child(userId)
        .updateChildren(
            mapOf(
                "milestone" to milestoneId,
                "goal" to null
            )
        )
        .asUnitSingle()
        .leftOnThrow()
        .toMaybe()
        .flatMapRightWithEither {
            if (previousMilestoneId.isNullOrBlank())
                Maybe.just(it.right())
            else
                milestoneDatabase
                    .child(userId)
                    .child(previousMilestoneId)
                    .updateChildren(mapOf("resolved_at" to Calendar.getInstance().timeInMillis))
                    .asUnitSingle()
                    .toMaybe()
                    .leftOnThrow()
        }
        .flatMapRightWithEither { getUserMetadata(userId) }
        .subscribeOn(networkScheduler)

    override fun setCurrentHabit(
        userId: String,
        habitId: String
    ): Maybe<Either<BaseError, UserMetadataResponse>> = handleHabit(userId, habitId)
        .flatMapRightWithEither { getUserMetadata(userId) }
        .subscribeOn(networkScheduler)


    override fun clearHabit(
        userId: String,
        habitId: String,
        addAsTopTier: Boolean
    ): Maybe<Either<BaseError, UserMetadataResponse>> = handleHabit(userId, null)
        .flatMapRightWithEither { if(addAsTopTier) addTopTierHabit(userId, habitId) else removeTopTierHabit(userId, habitId) }
        .subscribeOn(networkScheduler)

    override fun addTopTierHabit(
        userId: String,
        habitId: String
    ): Maybe<Either<BaseError, UserMetadataResponse>> = topTierHabitsDatabase
        .child(userId)
        .child(habitId)
        .setValue(true)
        .asUnitSingle()
        .toMaybe()
        .flatMap { getUserMetadata(userId) }
        .subscribeOn(networkScheduler)

    override fun removeTopTierHabit(
        userId: String,
        habitId: String
    ): Maybe<Either<BaseError, UserMetadataResponse>> = topTierHabitsDatabase
        .child(userId)
        .child(habitId)
        .removeValue()
        .asUnitSingle()
        .toMaybe()
        .flatMap { getUserMetadata(userId) }
        .subscribeOn(networkScheduler)

    private fun handleHabit(userId: String, habitId: String?) =
        if (!habitId.isNullOrBlank()) {
            milestoneOfHabitDatabase
                .child(userId)
                .child(habitId)
                .get()
                .toSingle()
                .toRx3()
                .toMaybe()
                .map { it.children.map { it.key!! } }
                .toObservable()
                .flatMapIterable { it }
                .concatMapSingle { milestoneId ->
                    milestoneDatabase
                        .child(userId)
                        .child(milestoneId)
                        .child("resolved_at")
                        .get()
                        .toSingle()
                        .toRx3()
                        .map { if (it.value == null) milestoneId.some() else none<String>() }
                }
                .toList()
                .toMaybe()
                .map { it.firstOrNull { it.isDefined() }?.orNull().toOption() }
        } else {
            Maybe.just(none())
        }
            .flatMap { milestoneId ->
                userDatabase.child(userId)
                    .updateChildren(
                        mapOf(
                            "habit" to habitId,
                            "goal" to null,
                            "milestone" to milestoneId.orNull()
                        )
                    )
                    .asUnitSingle()
                    .leftOnThrow()
                    .toMaybe()
            }

    private fun getTopTierHabits(userId: String) = milestoneOfHabitDatabase
        .child(userId)
        .get()
        .toSingle()
        .toRx3()
        .toMaybe()
        .map { it.children.map { it.key!! } }
        .subscribeOn(networkScheduler)
}

@IgnoreExtraProperties
private class ParsableUserMetadataResponse(
    @get:PropertyName("habit") @set:PropertyName("habit") var habit: String? = null,
    @get:PropertyName("milestone") @set:PropertyName("milestone") var milestone: String? = null,
    @get:PropertyName("goal") @set:PropertyName("goal") var goal: String? = null,
) {

    fun toUserMetadataResponse(userId: String, extraHabits: List<String>) = UserMetadataResponse(
        userId = userId,
        habitId = habit,
        milestoneId = milestone,
        goalId = goal,
        topTierHabitsIds = extraHabits.ifEmpty { null }
    )
}