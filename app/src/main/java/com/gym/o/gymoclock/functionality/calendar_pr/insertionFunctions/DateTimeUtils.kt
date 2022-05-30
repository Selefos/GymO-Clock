package com.gym.o.gymoclock.functionality.calendar_pr.insertionFunctions

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class DateTimeUtils {
    private val currentDateTime: LocalDateTime = LocalDateTime.now()

    fun getCurrentYear(): Int {
        val calendar: Calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }

    fun getCurrentMonth(): String {
        val monthNameFormat = DateTimeFormatter.ofPattern("MMMM")
        return currentDateTime.format(monthNameFormat)
    }

    fun getDate(): String{
        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDateTime.format(dateFormat)
    }

    fun getCurrentTime(): String{
        val statTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentDateTime.format(statTimeFormat)
    }

}