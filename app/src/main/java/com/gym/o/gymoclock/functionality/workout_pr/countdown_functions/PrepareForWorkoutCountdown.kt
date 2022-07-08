package com.gym.o.gymoclock.functionality.workout_pr.countdown_functions

import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.functionality.workout_pr.recyclerPosition
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.TextToSpeechUtils
import java.util.*

var prepareCountdownInMillis: Long = 0
private lateinit var prepareTimer: CountDownTimer
var isPrepareCountdown = true

fun WorkoutFragment.prepareForWorkoutTimer() {
    dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
    val inflater = layoutInflater //LayoutInflater.from(requireContext())
    val view = inflater.inflate(R.layout.workout_prepare, null)
    val prepareCountdownText = view.findViewById<TextView>(R.id.prepare_countdown)

    binding.roundsPicker.isEnabled = false
    startPrepareTimer(prepareCountdownText)

    dialogBuilder.setView(view)
    dialog = dialogBuilder.create()
    dialog.setCanceledOnTouchOutside(false)
    dialog.show()

}

fun WorkoutFragment.startPrepareTimer(textView: TextView) {

    prepareCountdownInMillis = 5000
    TextToSpeechUtils.getInstance(requireContext()).speak("Prepare")

    prepareTimer = object : CountDownTimer(prepareCountdownInMillis, 1000) {
        override fun onTick(millsUntilFinish: Long) {
            prepareCountdownInMillis = millsUntilFinish

            updatePrepareTimerUI(textView)
        }

        override fun onFinish() {
            isPrepareCountdown = false

            binding.playPauseButton.background = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_pause_button)

            startTotalTimer()
            listAdapter.startExerciseTimer(recyclerPosition)

            dialog.dismiss()
        }
    }.start()
}

fun WorkoutFragment.updatePrepareTimerUI(textView: TextView) {
    val secondsWork = (prepareCountdownInMillis / 1000) % 60
    val totalCount: String =
        String.format(Locale.getDefault(), "%02d", secondsWork)
    textView.text = totalCount

    if (prepareCountdownInMillis / 1000 <= 3)
        TextToSpeechUtils.getInstance(requireContext())
                .speak((prepareCountdownInMillis / 1000).toString())

    if (prepareCountdownInMillis / 1000 == 0L) {
        TextToSpeechUtils.getInstance(requireContext()).speak("Start")
        textView.text = resources.getString(R.string.workout_start)
    }

}