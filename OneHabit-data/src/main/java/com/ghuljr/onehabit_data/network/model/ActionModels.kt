package com.ghuljr.onehabit_data.network.model

data class ActionResponse(
    val title: String,
    val time: String,
    val quantityNumber: Int?,
    val quantityMax: Int?,
    val isCustom: Boolean,
    val state: String
)