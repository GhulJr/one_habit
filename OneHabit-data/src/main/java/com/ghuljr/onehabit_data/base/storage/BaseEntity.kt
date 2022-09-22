package com.ghuljr.onehabit_data.base.storage

import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Id

interface BaseEntity{
    var objectBoxId: Long
    var id: String
    var userId: String
    var dueToInMillis: Long
}