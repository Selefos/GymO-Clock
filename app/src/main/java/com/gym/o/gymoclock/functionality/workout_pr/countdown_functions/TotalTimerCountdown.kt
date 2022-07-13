package com.gym.o.gymoclock.functionality.workout_pr.countdown_functions

import android.os.CountDownTimer
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.FormatUtils
import java.util.*

private lateinit var totalTimer: CountDownTimer
var totalTimeInMillis: Long = 0 //by Delegates.notNull<Long>()
var endTimeTotalTimer: Long = 0//by Delegates.notNull<Long>()

fun WorkoutFragment.startTotalTimer() {

    totalTimeInMillis = FormatUtils.convertTimeToMillis(binding.totalTime.text.toString())
    endTimeTotalTimer = System.currentTimeMillis() + totalTimeInMillis

    totalTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
        override fun onTick(millsUntilFinish: Long) {
            totalTimeInMillis = millsUntilFinish

            updateTotalTimerUI()
        }

        override fun onFinish() {
        }
    }.start()
}

fun WorkoutFragment.updateTotalTimerUI() {
    val minutesWork = (totalTimeInMillis / 1000) / 60
    val secondsWork = (totalTimeInMillis / 1000) % 60
    val totalCount: String =
        String.format(Locale.getDefault(), "%02d:%02d", minutesWork, secondsWork)
    binding.totalTime.text = totalCount
}

fun WorkoutFragment.pauseTotalTimer() {
    totalTimer.cancel()
}