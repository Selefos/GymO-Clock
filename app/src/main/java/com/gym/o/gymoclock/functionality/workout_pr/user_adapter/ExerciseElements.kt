package com.gym.o.gymoclock.functionality.workout_pr.user_adapter

import android.os.CountDownTimer
import android.widget.TextView

data class ExerciseElements(
    var exerciseNameValue: String,
    var exerciseClockValue: TextView,
    var restClockValue: TextView,
    var wCountDownTimer: CountDownTimer,
    var rCountDownTimer: CountDownTimer,
    var wTimerIsRunning: Boolean,
    var wTimerIsPaused: Boolean,
    var rTimerIsRunning: Boolean,
    var rTimerIsPaused: Boolean,
)