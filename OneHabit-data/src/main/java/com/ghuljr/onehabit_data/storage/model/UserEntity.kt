package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.EntityHolder
import com.ghuljr.onehabit_data.base.storage.IndexedEntity
import io.objectbox.annotation.*

@Entity
data class UserEntity(
    @Id override var objectBoxId: Long = 0,
    @Unique(onConflict = ConflictStrategy.REPLACE) override var userId: String,
    override var id: String = userId,
    var habitId: String?,
    var milestoneId: String?,
    var goalId: String?,
    var extraHabits: List<String>?
) : IndexedEntity

@Entity
data class UserEntityHolder(
    @Id override var objectBoxId: Long = 0,
    @Unique(onConflict = ConflictStrategy.REPLACE) override var userId: String,
    override var dueToInMillis: Long
): EntityHolder
