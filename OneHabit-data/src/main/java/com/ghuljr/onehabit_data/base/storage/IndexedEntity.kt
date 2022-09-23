package com.ghuljr.onehabit_data.base.storage


interface BaseEntity {
    var objectBoxId: Long
    var userId: String
}
interface IndexedEntity : BaseEntity {
    var id: String
}

interface EntityHolder : BaseEntity {
    var dueToInMillis: Long
}