package com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality

import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.enums.PrepareTimerState
import com.gym.o.gymoclock.enums.PrepareTimerStateEnum
import com.gym.o.gymoclock.functionality.workout_pr.animations.fabFadeOutFadeInAnimation
import com.gym.o.gymoclock.functionality.workout_pr.animations.foldSettingsAnimation
import com.gym.o.gymoclock.functionality.workout_pr.fab_ui.numericalFABPosition
import com.gym.o.gymoclock.functionality.workout_pr.prepareCountdownInMillis
import com.gym.o.gymoclock.functionality.workout_pr.recyclerPosition
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseRecyclerAdapter
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils
import com.gym.o.gymoclock.utils.TextToSpeechUtils


private lateinit var prepareTimer: CountDownTimer

fun WorkoutFragment.prepareForWorkout() {

    val inflater = layoutInflater
    val view = inflater.inflate(R.layout.workout_prepare, null)
    val prepareCountdownText = view.findViewById<TextView>(R.id.prepare_countdown)

    startPrepareTimer(prepareCountdownText)

    dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
    dialogBuilder.setView(view)
    dialog = dialogBuilder.create()
    dialog.setCanceledOnTouchOutside(false)
    dialog.show()

}

fun WorkoutFragment.startPrepareTimer(textView: TextView) {

    TextToSpeechUtils.getInstance(requireContext()).speak("Prepare")

    prepareTimer = object : CountDownTimer(prepareCountdownInMillis, 1000) {
        override fun onTick(millsUntilFinish: Long) {
            prepareCountdownInMillis = millsUntilFinish

            updatePrepareTimerUI(textView)
        }

        override fun onFinish() {
            PrepareTimerState.prepareTimerState = PrepareTimerStateEnum.Working
            binding.playPauseFab.setImageResource(android.R.drawable.ic_media_pause)
            startTotalTimer()
            listAdapter.startExerciseTimer(recyclerPosition)

            dialog.dismiss()
        }
    }.start()
}

fun WorkoutFragment.updatePrepareTimerUI(textView: TextView) {
    textView.text = FormatUtils.timeInMillisecondsClockUI(prepareCountdownInMillis)

    if (prepareCountdownInMillis / 1000 <= 3)
        TextToSpeechUtils.getInstance(requireContext()).speak((prepareCountdownInMillis / 1000).toString())

    if (prepareCountdownInMillis / 1000 == 0L) {
        TextToSpeechUtils.getInstance(requireContext()).speak("Start")
        textView.text = resources.getString(R.string.workout_start)
    }

}

fun WorkoutFragment.responsiveUIElementsStateOnWorkout(stateEnabled: Boolean) {

    binding.roundsPicker.isEnabled = stateEnabled
    binding.addLayoutFab.isEnabled = stateEnabled

    if (!stateEnabled)
        disableAddLayoutFAB()
    else
        enableAddLayoutFAB()

    exerciseSettingsButtonState(stateEnabled)

}

fun WorkoutFragment.disableAddLayoutFAB(){
    binding.addLayoutFab.setColorFilter(Color.rgb(155, 50, 50))//red

    if(SharedPreferencesUtils.getLayoutAnimationsState(requireContext())) {
        fabFadeOutFadeInAnimation(binding.addLayoutFab)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                numericalFABPosition(binding.addLayoutFab, 30.0f, 30.0f)
            }, 1000)
        return
    }

    numericalFABPosition(binding.addLayoutFab, 30.0f, 30.0f)
}

fun WorkoutFragment.enableAddLayoutFAB(){
    binding.addLayoutFab.setColorFilter(Color.rgb(145, 251, 218))//green

    val addLayoutFABX = SharedPreferencesUtils.getAddLayoutFABPositionX(requireContext())
    val addLayoutFABY = SharedPreferencesUtils.getAddLayoutFABPositionY(requireContext())

    if(SharedPreferencesUtils.getLayoutAnimationsState(requireContext())) {
        fabFadeOutFadeInAnimation(binding.addLayoutFab)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                numericalFABPosition(binding.addLayoutFab, addLayoutFABX, addLayoutFABY)
            }, 1000)
        return
    }

    numericalFABPosition(binding.addLayoutFab, addLayoutFABX, addLayoutFABY)

}


fun WorkoutFragment.exerciseSettingsButtonState(stateEnabled: Boolean) {

    var holder: ExerciseRecyclerAdapter.ViewHolder?
    for (i: Int in 0 until listAdapter.itemCount) {
        holder = (recyclerView.adapter as ExerciseRecyclerAdapter).getViewByPosition(i)
        holder?.exerciseSettingsButton?.isEnabled = stateEnabled

        if (holder?.isSettingsVisible == true)
            foldSettingsAnimation(requireContext(), holder.exerciseSettingsButton, holder.editView, holder.removeView)

    }

}