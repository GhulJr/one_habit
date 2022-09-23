package com.ghuljr.onehabit_data.base.storage


interface BaseEntity {
    var objectBoxId: Long
    var userId: String
    var id: String
}

interface BaseEntityHolder {
    var objectBoxId: Long
    var userId: String
    var dueToInMillis: Long
}