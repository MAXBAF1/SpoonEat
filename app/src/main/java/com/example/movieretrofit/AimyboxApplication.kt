package com.example.movieretrofit

import android.app.Application
import android.content.Context
import com.example.movieretrofit.utils.ChangeViewSkill
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxDialogApi
import com.justai.aimybox.components.AimyboxProvider
import com.justai.aimybox.core.Config
import com.justai.aimybox.speechkit.google.platform.GooglePlatformSpeechToText
import com.justai.aimybox.speechkit.google.platform.GooglePlatformTextToSpeech
import java.util.*

class AimyboxApplication : Application(), AimyboxProvider {

    //rm8HkoUj8r6FehOPOv5XvbnAGCIdAZMT
    companion object {
        private const val AIMYBOX_API_KEY = "Ldf0j7WZi3KwNah2aNeXVIACz0lb9qMH"
    }

    override val aimybox by lazy { createAimybox(this) }

    private fun createAimybox(context: Context): Aimybox {
        val unitId = UUID.randomUUID().toString()
        val textToSpeech = GooglePlatformTextToSpeech(context, Locale.ENGLISH)
        val speechToText = GooglePlatformSpeechToText(context, Locale.ENGLISH)
        val dialogApi = AimyboxDialogApi(
            "<AIMYBOX_API_KEY>",
            unitId,
            customSkills = linkedSetOf(ChangeViewSkill(context))
        )

        val config = Config.create(speechToText = speechToText, textToSpeech = textToSpeech, dialogApi = dialogApi)

        return Aimybox(config, context)
    }
}