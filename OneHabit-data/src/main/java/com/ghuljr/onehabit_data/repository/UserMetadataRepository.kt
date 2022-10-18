package com.ghuljr.onehabit_data.repository

import arrow.core.Either
import arrow.core.right
import com.ghuljr.onehabit_data.cache.memory.MemoryCache
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.domain.UserMetadata
import com.ghuljr.onehabit_data.network.model.UserMetadataResponse
import com.ghuljr.onehabit_data.network.service.UserService
import com.ghuljr.onehabit_data.storage.model.UserEntity
import com.ghuljr.onehabit_data.storage.persistence.UserMetadataDatabase
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import com.ghuljr.onehabit_tools.di.NetworkScheduler
import com.ghuljr.onehabit_tools.extension.*
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserMetadataRepository @Inject constructor(
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

    fun setCurrentGoal(goalId: String): Maybe<Either<BaseError, UserMetadata>> = cache.get()
        .firstElement()
        .flatMapRightWithEither {
            it.dataFlowable
                .firstElement()
                .flatMapRightWithEither { user -> userService.setCurrentGoal(user.id, goalId) }
                .mapRight { userResponse ->
                    val entity = userResponse.toUserEntity()
                    userMetadataDatabase.put(entity)
                    entity.toDomain()
                }
        }

    fun setCurrentHabit(habitId: String): Maybe<Either<BaseError, UserMetadata>> = cache.get()
        .firstElement()
        .flatMapRightWithEither {
            it.dataFlowable
                .firstElement()
                .flatMapRightWithEither { user -> userService.setCurrentHabit(user.id, habitId) }
                .mapRight { userResponse ->
                    val entity = userResponse.toUserEntity()
                    userMetadataDatabase.put(entity)
                    entity.toDomain()
                }
        }

    fun setCurrentMilestone(milestoneId: String): Maybe<Either<BaseError, UserMetadata>> =
        cache.get()
            .firstElement()
            .flatMapRightWithEither {
                it.dataFlowable
                    .firstElement()
                    .flatMapRightWithEither { user -> userService.setCurrentMilestone(user.id, user.milestoneId, milestoneId) }
                    .mapRight { userResponse ->
                        val entity = userResponse.toUserEntity()
                        userMetadataDatabase.put(entity)
                        entity.toDomain()
                    }
            }

    fun clearCurrentHabit(shouldAddAsEndTier: Boolean): Maybe<Either<BaseError, UserMetadata>> =
        cache.get()
            .firstElement()
            .flatMapRightWithEither {
                it.dataFlowable
                    .firstElement()
                    .flatMapRightWithEither { user ->
                        if (user.habitId.isNullOrBlank())
                            Maybe.just(user.toDomain().right())
                        else
                            userService.clearHabit(user.userId, user.habitId!!, shouldAddAsEndTier)
                                .mapRight { userResponse ->
                                    val entity = userResponse.toUserEntity()
                                    userMetadataDatabase.put(entity)
                                    entity.toDomain()
                                }
                    }
            }
}

private fun UserMetadataResponse.toUserEntity() = UserEntity(
    userId = userId,
    habitId = habitId,
    milestoneId = milestoneId,
    goalId = goalId,
    extraHabits = topTierHabitsIds
)

private fun UserEntity.toDomain() = UserMetadata(
    id = id,
    habitId = habitId,
    milestoneId = milestoneId,
    goalId = goalId,
    extraHabits = extraHabits
)