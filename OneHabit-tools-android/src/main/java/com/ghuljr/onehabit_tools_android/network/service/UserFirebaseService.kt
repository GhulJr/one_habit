package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.Either
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserFirebaseService @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler
) : UserService {

    private val userDatabase = Firebase.database.getReference("user")

    override fun getUserMetadata(userId: String): Maybe<Either<BaseError, UserMetadataResponse>> =
        userDatabase.child(userId)
            .get()
            .toSingle()
            .toRx3()
            .toMaybe()
            .map {
                it.getValue(ParsableUserMetadataResponse::class.java)!!
                    .toUserMetadataResponse(userId)
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
        .flatMapRightWithEither { getUserMetadata(userId) }
        .subscribeOn(networkScheduler)

    override fun setCurrentHabit(
        userId: String,
        habitId: String
    ): Maybe<Either<BaseError, UserMetadataResponse>> = userDatabase.child(userId)
        .updateChildren(
            mapOf(
                "habit" to habitId,
                "goal" to null,
                "milestone" to null
            )
        )
        .asUnitSingle()
        .leftOnThrow()
        .toMaybe()
        .flatMapRightWithEither { getUserMetadata(userId) }
        .subscribeOn(networkScheduler)
}

@IgnoreExtraProperties
private class ParsableUserMetadataResponse(
    @get:PropertyName("habit") @set:PropertyName("habit") var habit: String? = null,
    @get:PropertyName("milestone") @set:PropertyName("milestone") var milestone: String? = null,
    @get:PropertyName("goal") @set:PropertyName("goal") var goal: String? = null,
    @get:PropertyName("extra_habits") @set:PropertyName("extra_habits") var extraHabits: List<String>? = null
) {

    fun toUserMetadataResponse(userId: String) = UserMetadataResponse(
        userId = userId,
        habitId = habit,
        milestoneId = milestone,
        goalId = goal,
        extraHabitsIds = extraHabits
    )
}