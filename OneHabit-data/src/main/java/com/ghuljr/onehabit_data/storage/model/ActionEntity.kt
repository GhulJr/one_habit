package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.EntityHolder
import com.ghuljr.onehabit_data.base.storage.IndexedEntity
import io.objectbox.BoxStore
import io.objectbox.annotation.*
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
data class ActionEntity(
    @Id override var objectBoxId: Long = 0,
    @Index @Unique(onConflict = ConflictStrategy.REPLACE) override var id: String,
    override var userId: String,
    var remindersAtMs: List<String>?,
    var currentRepeat: Int,
    var totalRepeats: Int
) : IndexedEntity

@Entity
data class ActionOfGoalEntitiesHolder(
    @Id override var objectBoxId: Long = 0,
    override var userId: String,
    override var dueToInMillis: Long,
    var actionIds: List<String>,
    @Index @Unique(onConflict = ConflictStrategy.REPLACE) var goalId: String
) : EntityHolder {
}