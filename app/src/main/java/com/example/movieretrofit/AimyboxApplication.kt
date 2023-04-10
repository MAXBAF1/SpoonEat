package com.example.movieretrofit

import android.app.Application
import android.content.Context
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxDialogApi
import com.justai.aimybox.components.AimyboxProvider
import com.justai.aimybox.core.Config
import com.justai.aimybox.speechkit.google.platform.GooglePlatformSpeechToText
import com.justai.aimybox.speechkit.google.platform.GooglePlatformTextToSpeech
import java.util.*

class AimyboxApplication : Application(), AimyboxProvider {
    companion object {
        private const val AIMYBOX_API_KEY = "rm8HkoUj8r6FehOPOv5XvbnAGCIdAZMT"
    }

    override val aimybox by lazy { createAimybox(this) }

    private fun createAimybox(context: Context): Aimybox {
        val unitId = UUID.randomUUID().toString()

        val textToSpeech = GooglePlatformTextToSpeech(context, Locale("ru"))
        val speechToText = GooglePlatformSpeechToText(context, Locale("ru"))

        val dialogApi = AimyboxDialogApi(AIMYBOX_API_KEY, unitId)

        val aimyboxConfig = Config.create(speechToText, textToSpeech, dialogApi)
        return Aimybox(aimyboxConfig)
    }
}