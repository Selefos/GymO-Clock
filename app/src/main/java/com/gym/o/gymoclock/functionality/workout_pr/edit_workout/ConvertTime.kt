package com.gym.o.gymoclock.functionality.workout_pr.edit_workout

import java.util.*
import kotlin.properties.Delegates

object ConvertTime {
    private var minutes by Delegates.notNull<Long>()
    private var seconds by Delegates.notNull<Long>()

    fun convertTimeToDigitalClock(time: String): String {
        var convertedWorkTime: String = String.format("%02d:%02d", 0, 0)
        return if (time.isNotEmpty()) {
            minutes = time.toLong() / 60
            seconds = time.toLong() % 60
            convertedWorkTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            convertedWorkTime
        } else convertedWorkTime
    }

    fun convertTimeToMillis(time: String): Long {
        val units = time.split(":")
        val min = Integer.parseInt(units[0])
        val secs = Integer.parseInt(units[1])
        return (60 * min + secs).toLong() * 1000
    }

    fun convertTimeToSeconds(time: String): Long {
        val units = time.split(":")
        val min = Integer.parseInt(units[0])
        val secs = Integer.parseInt(units[1])
        return (60 * min + secs).toLong()
    }
}