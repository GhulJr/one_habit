package com.ghuljr.onehabit_data.storage.model

import com.ghuljr.onehabit_data.base.storage.IndexedEntity
import io.objectbox.annotation.ConflictStrategy
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique

data class MilestoneEntity(
    @Id override var objectBoxId: Long = 0,
    @Unique(onConflict = ConflictStrategy.REPLACE) override var id: String,
    @Index override var userId: String,
    @Index var habitId: String,
    var intensity: Long,
   // var
) : IndexedEntity