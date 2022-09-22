package com.ghuljr.onehabit_data.network.service

import com.ghuljr.onehabit_data.network.model.ActionResponse

interface ActionsService {

    fun getTodayActions(): List<ActionResponse>
}