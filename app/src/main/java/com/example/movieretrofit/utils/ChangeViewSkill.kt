package com.example.movieretrofit.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.movieretrofit.fragments.ui.AccountSettingsFragment
import com.example.movieretrofit.fragments.ui.HomeFragment
import com.example.movieretrofit.fragments.ui.SearchFragment
import com.example.movieretrofit.fragments.ui.StatisticsFragment
import com.example.movieretrofit.model.FoodViewModel
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxRequest
import com.justai.aimybox.api.aimybox.AimyboxResponse
import com.justai.aimybox.core.CustomSkill
import com.justai.aimybox.model.Request
import com.justai.aimybox.model.Response

class ChangeViewSkill(private val context: Context): CustomSkill<AimyboxRequest, AimyboxResponse> {

    lateinit var queryRequest: String
    override fun canHandle(response: AimyboxResponse) = response.action == "changeView"

    override suspend fun onRequest(request: AimyboxRequest, aimybox: Aimybox): AimyboxRequest {
        queryRequest = request.query

        Log.e("aimybox", "request query is $queryRequest")

        return request
    }
    override suspend fun onResponse(
        response: AimyboxResponse,
        aimybox: Aimybox,
        defaultHandler: suspend (Response) -> Unit
    ) {
        //TODO отправка в add_food
        Log.e("aimybox", "request query is $queryRequest")

    }
}