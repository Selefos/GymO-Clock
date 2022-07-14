package com.gym.o.gymoclock.utils

import java.util.*

class FormatUtils {
    companion object {

        private var minutes: Long = 0L
        private var seconds: Long = 0L

        fun convertTimeToDigitalClock(time: String): String {
            var convertedTime: String = String.format("%02d:%02d", 0, 0)
            return if (time.isNotEmpty()) {
                minutes = time.toLong() / 60
                seconds = time.toLong() % 60
                convertedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                convertedTime
            } else convertedTime
        }

        fun convertTimeToDigitalClockMinSecFormat(time: String): String {
            val units = time.split(":")
            val min = Integer.parseInt(units[0])
            val secs = Integer.parseInt(units[1])

            return "$min min, $secs secs"
        }

        fun convertTimeToDigitalClockMinutes(time: String): String {
            minutes = time.toLong()
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        fun convertTimeToDigitalClockSeconds(time: String): String {
            seconds = time.toLong()
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        fun convertTimeToMillis(time: String): Long {
            val units = time.split(":")
            val min = Integer.parseInt(units[0])
            val secs = Integer.parseInt(units[1])
            return (60 * min + secs).toLong() * 1000
        }

        fun convertTimeToSeconds(time: String): Int {
            val units = time.split(":")
            val min = Integer.parseInt(units[0])
            val secs = Integer.parseInt(units[1])
            return 60 * min + secs
        }

        fun prepareWorkoutTableStringSpDs(tableName: String): String {
            return tableName.replace(" ", "_")
        }

        fun prepareWorkoutTableStringDsSp(tableName: String): String {
            return tableName.replace("_", " ")
        }

    }
}