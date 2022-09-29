package com.ghuljr.onehabit_data.network.service

import arrow.core.Either
import com.ghuljr.onehabit_data.network.model.HabitRequest
import com.ghuljr.onehabit_data.network.model.HabitResponse
import com.ghuljr.onehabit_error.BaseError
import io.reactivex.rxjava3.core.Maybe

interface HabitService {
    fun getHabit(userId: String, habitId: String): Maybe<Either<BaseError, HabitResponse>>
    fun getAllHabits(userId: String): Maybe<Either<BaseError, List<HabitResponse>>>
    fun createHabit(habitRequest: HabitRequest) : Maybe<Either<BaseError, HabitResponse>>
}