package com.ghuljr.onehabit_tools.base.network

interface ActionsService {

    fun getTodayActions(): List<ActionResponse>
}

data class ActionResponse(
    val title: String,
    val time: String,
    val quantityNumber: Int?,
    val quantityMax: Int?,
    val isCustom: Boolean,
    val state: String // TODO: introduce some kind of enum? It is telling if it's finished or not
)