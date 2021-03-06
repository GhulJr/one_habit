package com.ghuljr.onehabit_data.base.storage

import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Id

@BaseEntity
abstract class BaseEntity<ID: Any>(
    @Id var objectBoxId: Long = 0,
    val dueToInMillis: Long = 0L,
    open val customId: ID
) {
}