package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.EntityHolder
import com.ghuljr.onehabit_data.base.storage.IndexedEntity
import io.objectbox.BoxStore
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
data class ActionEntity(
    @Id override var objectBoxId: Long = 0,
    @Index override var id: String,
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
    var goalId: String
) : EntityHolder {

    lateinit var actions: List<ActionEntity>

    constructor(
        objectBoxId: Long,
        userId: String,
        dueToInMillis: Long,
        goalId: String,
        actions: List<ActionEntity>
    ) : this(objectBoxId, userId, dueToInMillis, actions.map { it.id }, goalId) {
        this.actions = actions
    }
}