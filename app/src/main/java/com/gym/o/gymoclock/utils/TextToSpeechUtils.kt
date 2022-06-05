package com.gym.o.gymoclock.utils

import android.app.Application
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class TextToSpeechUtils(context: Context): Application(), TextToSpeech.OnInitListener{
    private var tts: TextToSpeech? = null
    private var textToSpeak: String? = null

    init {
        this.tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            if (tts!!.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                tts!!.language = Locale.US
            var pitch = 50.toFloat() / 50
            if (pitch < 0.1) pitch = 0.1f
            var speed = 50.toFloat() / 50
            if (speed < 0.1) speed = 0.1f
            tts!!.setPitch(pitch)
            tts!!.setSpeechRate(speed)

        }

        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Log.e("TTS", "Language not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }

    }

    fun speak(voiceText: String) {
        textToSpeak = voiceText
        if (tts == null) {
            // currently can\'t change Locale until speech ends
            try {
                var pitch = 50.toFloat() / 50
                if (pitch < 0.1) pitch = 0.1f
                var speed = 50.toFloat() / 50
                if (speed < 0.1) speed = 0.1f
                tts!!.setPitch(pitch)
                tts!!.setSpeechRate(speed)
                tts = TextToSpeech(applicationContext, this@TextToSpeechUtils)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            tts!!.speak(voiceText, TextToSpeech.QUEUE_FLUSH, null)
        else
            tts!!.speak(voiceText, TextToSpeech.QUEUE_FLUSH, null,null)

    }

    fun stopTTS() {
        if (tts != null) {
            tts!!.shutdown()
            tts!!.stop()
            tts = null
        }
    }

    companion object{

        private  var instance: TextToSpeechUtils? = null
        fun getInstance(context: Context): TextToSpeechUtils = synchronized(this){
            if (instance == null)
                instance = TextToSpeechUtils(context)
            return instance as TextToSpeechUtils
        }

    }

}
