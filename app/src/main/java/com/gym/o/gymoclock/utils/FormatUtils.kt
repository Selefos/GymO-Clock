package com.gym.o.gymoclock.utils

import java.util.*


class FormatUtils {

    companion object {

        private var hours: Long = 0L
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

        fun timeInMillisecondsClockUI(timeInMilliseconds: Long): String {
            val minutesWork = (timeInMilliseconds / 1000) / 60
            val secondsWork = (timeInMilliseconds / 1000) % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutesWork, secondsWork)
        }

        fun convertTimeToDigitalClockMinutes(time: String): String {
            minutes = time.toLong()
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        fun convertTimeToDigitalClockSeconds(time: String): String {
            seconds = time.toLong()
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        fun convertDigitalTimeToMillis(time: String): Long {
            val units = time.split(":")
            val min = Integer.parseInt(units[0])
            val secs = Integer.parseInt(units[1])
            return (60 * min + secs).toLong() * 1000
        }

        fun convertDigitalTimeToSeconds(time: String): Int {
            val units = time.split(":")
            val min = Integer.parseInt(units[0])
            val secs = Integer.parseInt(units[1])
            return 60 * min + secs
        }

        fun convertTotalTimeToDigitalClock(time: String): String {
            var convertedTime: String = String.format("%02d:%02d:%02d", 0, 0, 0)
            return if (time.isNotEmpty()) {

                hours = time.toLong() / 3600
                minutes = (time.toLong() % 3600) / 60
                seconds = time.toLong() % 60

                convertedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                convertedTime
            } else convertedTime
        }

        
        fun totalTimeInMillisecondsClockUI(timeInMilliseconds: Long): String {
            val secondsWork = (timeInMilliseconds / 1000) % 60
            val minutesWork = (timeInMilliseconds / (1000 * 60)) % 60
            val hoursWork = (timeInMilliseconds / (1000 * 60 * 60)) % 24
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hoursWork, minutesWork, secondsWork)
        }

        fun calendarTotalTimeToDigitalFormat(time: String): String {
            val units = time.split(":")
            val h = Integer.parseInt(units[0])
            val m = Integer.parseInt(units[1])
            val s = Integer.parseInt(units[2])
            return String.format(Locale.getDefault(), "%02dh %02dm %02ds", h, m, s)
        }

        fun convertDigitalTotalTimeToMillis(time: String): Long {
            val units = time.split(":")
            val hour = Integer.parseInt(units[0])
            val min = Integer.parseInt(units[1])
            val secs = Integer.parseInt(units[2])
            return (3600 * hour + 60 * min + secs).toLong() * 1000
        }


        fun stringSpaceToUnderscore(string: String): String {
            return string.replace(" ", "_")
        }

        fun stringUnderscoreToSpace(string: String): String {
            return string.replace("_", " ")
        }

        var strSeparator = ","
        fun convertArrayToString(array: Array<String>): String {
            var str = ""
            for (i in array.indices) {
                str += array[i]
                // Do not append comma at the end of last element
                if (i < array.size - 1) {
                    str += strSeparator
                }
            }
            return str
        }

        fun convertStringToArray(str: String): Array<String> {
            return str.split(strSeparator).toTypedArray()
        }

    }

}