package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.EntityHolder
import com.ghuljr.onehabit_data.base.storage.IndexedEntity
import io.objectbox.annotation.*

@Entity
data class GoalEntity(
    @Id override var objectBoxId: Long = 0,
    @Unique(onConflict = ConflictStrategy.REPLACE) override var id: String,
    @Index override var userId: String,
    @Index var milestoneId: String,
    var remindersAtMs: Long
) : IndexedEntity

@Entity
data class GoalEntityHolder(
    @Id override var objectBoxId: Long = 0,
    override var userId: String,
    override var dueToInMillis: Long,
    @Unique(onConflict = ConflictStrategy.REPLACE) var milestoneId: String
): EntityHolder
