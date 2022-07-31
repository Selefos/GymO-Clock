package com.gym.o.gymoclock.functionality.main_activity_pr.settings

import android.content.Context
import android.media.AudioManager
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.SeekBar
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.utils.DialogBuilderUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils
import com.gym.o.gymoclock.utils.TextToSpeechUtils


fun MainActivity.voiceAssist() {
    val dialogBuilderUtils = DialogBuilderUtils(this)
    dialogBuilderUtils.voiceAssistScope(false)

    dialogBuilderUtils.cancelButtonVoiceAssistSettings.setOnClickListener {
        dialogBuilderUtils.dialog.dismiss()
    }

    voiceAssistantState(dialogBuilderUtils)
    voiceTestButton(dialogBuilderUtils)
    volumeControl(dialogBuilderUtils)

}

fun MainActivity.voiceAssistantState(dialogBuilderUtils: DialogBuilderUtils){
    dialogBuilderUtils.voiceAssistState.isChecked = SharedPreferencesUtils.getTtsState(this)
    dialogBuilderUtils.voiceAssistState.isChecked = dialogBuilderUtils.voiceAssistState.isChecked

    if (dialogBuilderUtils.voiceAssistState.isChecked)
        dialogBuilderUtils.voiceAssistState.text = resources.getString(R.string.settings_disable_voice_assist)
    else
        dialogBuilderUtils.voiceAssistState.text = resources.getString(R.string.settings_enable_voice_assist)

    dialogBuilderUtils.voiceAssistState.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            dialogBuilderUtils.voiceAssistState.text = resources.getString(R.string.settings_disable_voice_assist)
            Log.i("IsChecked", isChecked.toString())
        } else {
            dialogBuilderUtils.voiceAssistState.text = resources.getString(R.string.settings_enable_voice_assist)
            Log.i("IsChecked", isChecked.toString())
        }
        SharedPreferencesUtils.saveTtsState(this, dialogBuilderUtils.voiceAssistState.isChecked)
        Log.i("IsChecked", isChecked.toString())
    }
}

fun MainActivity.voiceTestButton(dialogBuilderUtils: DialogBuilderUtils){
    var isPlayPressed = false
    dialogBuilderUtils.testVoiceVolumeButton.setOnClickListener {

        isPlayPressed = !isPlayPressed
        if (isPlayPressed) {
            TextToSpeechUtils.getInstance(applicationContext).speak(resources.getString(R.string.settings_test_button_text))
            TextToSpeechUtils.getInstance(applicationContext).tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

                override fun onStart(utteranceId: String) {
                    Log.i("TextToSpeech", "On Start")
                    runOnUiThread {
                        dialogBuilderUtils.testVoiceVolumeButton.text = applicationContext.resources.getString(R.string.settings_test_button_stop)
                        isPlayPressed = true
                    }

                }

                override fun onDone(utteranceId: String) {
                    Log.i("TextToSpeech", "On Done")
                    runOnUiThread {
                        dialogBuilderUtils.testVoiceVolumeButton.text = applicationContext.resources.getString(R.string.settings_test_button_play)
                        isPlayPressed = false
                    }
                }

                override fun onError(utteranceId: String) {
                    Log.i("TextToSpeech", "On Error")
                }
            })
        } else {
            dialogBuilderUtils.testVoiceVolumeButton.text = resources.getString(R.string.settings_test_button_play)
            TextToSpeechUtils.getInstance(this).stopTTS()
        }
    }
}

fun MainActivity.volumeControl(dialogBuilderUtils: DialogBuilderUtils){
    val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    //dialogBuilderUtils.volumeControl.max = maxVolume

    currentVolume = SharedPreferencesUtils.getVolume(applicationContext)
    dialogBuilderUtils.volumePercent.text = "$currentVolume%"
    dialogBuilderUtils.volumeControl.progress = currentVolume

    dialogBuilderUtils.volumeControl.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (progress*15)/100, 0)
            dialogBuilderUtils.volumePercent.text = "$progress%"
            currentVolume = progress
        }

        override fun onStartTrackingTouch(seek: SeekBar) {
        }

        override fun onStopTrackingTouch(seek: SeekBar) {
            SharedPreferencesUtils.saveVolume(applicationContext, currentVolume)
        }
    })
}