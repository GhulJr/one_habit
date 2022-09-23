package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.EntityHolder
import com.ghuljr.onehabit_data.base.storage.IndexedEntity
import io.objectbox.annotation.ConflictStrategy
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class HabitEntity(
    @Id override var objectBoxId: Long = 0,
    override var userId: String,
    @Unique(onConflict = ConflictStrategy.REPLACE) override var id: String,
    var currentProgress: Int,
    var defaultProgressFactor: Int,
    var defaultRemindersMs: List<String>?,
    var baseIntensity: Int,
    var desiredIntensity: Int,
    var title: String?,
    var description: String?,
    var type: String,
    var habitSubject: String,
    var settlingFormat: Int
) : IndexedEntity

@Entity
data class HabitEntityHolder(
    @Id override var objectBoxId: Long = 0,
    override var userId: String,
    override var dueToInMillis: Long,
    @Unique(onConflict = ConflictStrategy.REPLACE) val habitId: String
) : EntityHolder