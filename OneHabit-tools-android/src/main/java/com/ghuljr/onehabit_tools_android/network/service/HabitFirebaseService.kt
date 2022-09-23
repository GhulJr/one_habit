package com.ghuljr.onehabit_tools_android.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.HabitResponse
import com.ghuljr.onehabit_data.network.service.HabitService
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error_android.extension.leftOnThrow
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.toRx3
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
class HabitFirebaseService @Inject constructor(
    @NetworkScheduler private val networkScheduler: Scheduler
) : HabitService {

    private val habitDatabase = Firebase.database.getReference("habit")

    override fun getHabit(
        userId: String,
        habitId: String
    ): Maybe<Either<BaseError, HabitResponse>> = habitDatabase.child(userId)
        .child(habitId)
        .get()
        .toSingle()
        .toRx3()
        .toMaybe()
        .map { it.getValue(ParsableHabitResponse::class.java)!!.toHabitResponse(userId) }
        .leftOnThrow()
        .subscribeOn(networkScheduler)

    override fun getAllHabits(userId: String): Maybe<Either<BaseError, List<HabitResponse>>> {
        TODO("Not yet implemented")
    }


}

@IgnoreExtraProperties
private class ParsableHabitResponse(
    @get:PropertyName("current_progress") @set:PropertyName("current_progress") var currentProgress: Int = 0,
    @get:PropertyName("default_progress_factor") @set:PropertyName("default_progress_factor") var defaultProgressFactor: Int = 0,
    @get:PropertyName("default_reminders") @set:PropertyName("default_reminders") var defaultRemindersMs: List<Long>? = null,
    @get:PropertyName("intensity_baseline") @set:PropertyName("intensity_baseline") var baseIntensity: Int = 0,
    @get:PropertyName("intensity_desired") @set:PropertyName("intensity_desired") var desiredIntensity: Int = 0,
    @get:PropertyName("title") @set:PropertyName("title") var title: String? = null,
    @get:PropertyName("description") @set:PropertyName("description") var description: String? = null,
    @get:PropertyName("type") @set:PropertyName("type") var type: String? = null,
    @get:PropertyName("what") @set:PropertyName("what") var habitSubject: String? = null,
    @get:PropertyName("which_days") @set:PropertyName("which_days") var settlingFormat: Int = 0
) {
    fun toHabitResponse(userId: String) = HabitResponse(
        userId = userId,
        currentProgress = currentProgress,
        defaultProgressFactor = defaultProgressFactor,
        defaultRemindersMs = defaultRemindersMs,
        baseIntensity = baseIntensity,
        desiredIntensity = desiredIntensity,
        title = title,
        description = description,
        type = type!!,
        habitSubject = habitSubject!!,
        settlingFormat = settlingFormat
    )
}