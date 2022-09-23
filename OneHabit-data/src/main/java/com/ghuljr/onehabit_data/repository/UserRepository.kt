package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.UserMetadata
import com.ghuljr.onehabit_data.network.model.UserMetadataResponse
import com.ghuljr.onehabit_data.network.service.UserService
import com.ghuljr.onehabit_data.storage.model.UserEntity
import com.ghuljr.onehabit_data.storage.persistence.UserMetadataDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error.LoggedOutError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.flatMapRightWithEither
import com.ghuljr.onehabit_tools.extension.mapRight
import com.ghuljr.onehabit_tools.extension.switchMapRightWithEither
import com.ghuljr.onehabit_tools.extension.toEither
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// TODO: add mechanics to ask user to select at least one habit
@Singleton
class UserRepository @Inject constructor(
    @ComputationScheduler private val computationScheduler: Scheduler,
    @NetworkScheduler private val networkScheduler: Scheduler,
    private val userService: UserService,
    private val userMetadataDatabase: UserMetadataDatabase,
    private val memoryCacheFactory: MemoryCache.Factory<String, DataSource<UserEntity>>
) {

    private val cache = memoryCacheFactory.create { key ->
        DataSource(
            refreshInterval = 1,
            refreshIntervalUnit = TimeUnit.DAYS,
            cachedDataFlowable = userMetadataDatabase.userMetadata(key.userId),
            fetch = {
                userService.getUserMetadata(key.userId)
                    .toSingle()
                    .mapRight { it.toUserEntity() }
            },
            invalidateAndUpdate = { data ->
                userMetadataDatabase.replaceUser(key.userId, data.value.orNull(), data.dueToInMillis)
            },
            networkScheduler = networkScheduler,
            computationScheduler = computationScheduler
        )
    }

    val currentUser: Observable<Either<BaseError, UserMetadata>> = cache.get()
        .switchMapRightWithEither { it.dataFlowable.mapRight { it.toDomain() } }
        .toObservable()
        .replay(1)
        .refCount()

    fun refreshUser(): Maybe<Either<BaseError, UserMetadata>> = cache.get()
        .firstElement()
        .flatMapRightWithEither { it.refresh() }
        .mapRight { it.toDomain() }
}

private fun UserMetadataResponse.toUserEntity() = UserEntity(
    userId = userId,
    habitId = habitId,
    milestoneId = milestoneId,
    goalId = goalId,
    extraHabits = extraHabitsIds
)

private fun UserEntity.toDomain() = UserMetadata(
    habitId = habitId,
    milestoneId = milestoneId,
    goalId = goalId,
    extraHabits = extraHabits
)