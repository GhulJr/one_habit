package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.BaseEntity
import com.ghuljr.onehabit_data.base.storage.BaseEntityHolder
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
    var remindersAtMs: List<String>,
    var currentRepeat: Int,
    var totalRepeats: Int,
    var cacheHolder: ToOne<ActionEntitiesHolder>
    // TODO: relation with goals
) : BaseEntity

@Entity
data class ActionEntitiesHolder(
    @Id override var objectBoxId: Long = 0,
    override var userId: String,
    override var dueToInMillis: Long,
    @Backlink var actions: ToMany<ActionEntity>
) : BaseEntityHolder