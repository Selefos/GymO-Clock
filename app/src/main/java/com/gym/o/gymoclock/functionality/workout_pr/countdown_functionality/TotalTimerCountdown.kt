package com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality

import android.os.CountDownTimer
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.FormatUtils

private lateinit var totalTimer: CountDownTimer
var totalTimeInMillis: Long = 0 //by Delegates.notNull<Long>()
var endTimeTotalTimer: Long = 0//by Delegates.notNull<Long>()

fun WorkoutFragment.startTotalTimer() {

    totalTimeInMillis = FormatUtils.convertDigitalTimeToMillis(binding.totalTime.text.toString())
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
    binding.totalTime.text = FormatUtils.timeInMillisecondsClockUI(totalTimeInMillis)
}

fun pauseTotalTimer() {
    totalTimer.cancel()
}