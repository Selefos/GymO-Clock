package com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter

import android.os.CountDownTimer
import android.widget.EditText
import android.widget.TextView

data class ExerciseElements(
    var exerciseNameValue: String,
    var exerciseClockValue: TextView,
    var restClockValue: TextView,
    var insertWeight: EditText,
    val insertReps: EditText,
    var wCountDownTimer: CountDownTimer,
    var rCountDownTimer: CountDownTimer,
    var wTimerIsRunning: Boolean,
    var wTimerIsPaused: Boolean,
    var rTimerIsRunning: Boolean,
    var rTimerIsPaused: Boolean,
)