package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.MilestoneResponse
import com.ghuljr.onehabit_data.network.service.MilestoneService
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
class MilestoneFirebaseService @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler
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
        .map { it.getValue(ParsableMilestoneResponse::class.java)!!.toResponse(milestoneId, userId) }
        .toMaybe()
        .leftOnThrow()
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

}

@IgnoreExtraProperties
private data class ParsableMilestoneResponse(
    @get:PropertyName("intensity") @set:PropertyName("intensity") var intensity: Int = 0,
    @get:PropertyName("order_number") @set:PropertyName("order_number") var orderNumber: Int = 0,
    @get:PropertyName("resolved") @set:PropertyName("resolved") var resolved: Boolean = false
) {
    fun toResponse(id: String, userId: String) = MilestoneResponse(
        id = id,
        userId = userId,
        intensity = intensity,
        orderNumber = orderNumber,
        resolved = resolved
    )
}