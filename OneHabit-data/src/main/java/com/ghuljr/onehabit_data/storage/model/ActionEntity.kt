package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.BaseEntity
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class ActionEntity(
    @Id override var objectBoxId: Long = 0,
    @Index override var id: String,
    override var userId: String,
    override var dueToInMillis: Long,
    var remindersAtMs: List<String>,
    var currentRepeat: Int,
    var totalRepeats: Int
    // TODO: relation with goals
) : BaseEntity