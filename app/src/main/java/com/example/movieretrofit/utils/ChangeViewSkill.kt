package com.example.movieretrofit.utils

import android.content.Context
import android.content.Intent
import com.example.movieretrofit.fragments.ui.AccountSettingsFragment
import com.example.movieretrofit.fragments.ui.HomeFragment
import com.example.movieretrofit.fragments.ui.SearchFragment
import com.example.movieretrofit.fragments.ui.StatisticsFragment
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxRequest
import com.justai.aimybox.api.aimybox.AimyboxResponse
import com.justai.aimybox.core.CustomSkill
import com.justai.aimybox.model.Response

class ChangeViewSkill(private val context: Context): CustomSkill<AimyboxRequest, AimyboxResponse> {

    override fun canHandle(response: AimyboxResponse) = response.action == "changeView"

    override suspend fun onResponse(
        response: AimyboxResponse,
        aimybox: Aimybox,
        defaultHandler: suspend (Response) -> Unit
    ) {
        val intent = when (response.intent) {
            "settings" -> Intent(context, AccountSettingsFragment::class.java)
            "statistic" -> Intent(context, StatisticsFragment::class.java)//
            "profile" -> Intent(context, AccountSettingsFragment::class.java)//
            "food" -> Intent(context, SearchFragment::class.java)
            else -> Intent(context, HomeFragment::class.java)
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        aimybox.standby()
        context.startActivity(intent)
    }
}