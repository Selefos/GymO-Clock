package com.gym.o.gymoclock.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {
    companion object {

        //private val currentDateTime: LocalDateTime = LocalDateTime.now()

        fun getCurrentYear(): String {
//        val calendar: Calendar = Calendar.getInstance()
//        Log.i("DateTimeUtils_Month", calendar.get(Calendar.YEAR).toString())
//        return calendar.get(Calendar.YEAR)
            val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            //Log.i("DateTimeUtils_Year", yearFormat.format(Date()))
            return yearFormat.format(Date())
        }

        fun getCurrentMonth(): String {
            //val monthNameFormat = DateTimeFormatter.ofPattern("MMMM")
            //return currentDateTime.format(monthNameFormat)
            val monthName = SimpleDateFormat("MMMM", Locale.getDefault())
            //Log.i("DateTimeUtils_Month", monthName.format(Date()).uppercase())
            return monthName.format(Date()).uppercase()
        }

        fun setCalendarTableName(): String {
            //Log.i("DateTimeUtils_MonthYear", "${getCurrentMonth()} ${getCurrentYear()}".replace(" ", "_"))
            return "${getCurrentMonth()} ${getCurrentYear()}".replace(" ", "_")
        }

        fun getDate(): String {
//        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
//        return currentDateTime.format(dateFormat)

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            //Log.i("DateTimeUtils_Date", dateFormat.format(Date()))
            return dateFormat.format(Date())
        }

        fun getCurrentTime(): String {
//        val startTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")
//        return currentDateTime.format(statTimeFormat)
            val startTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            //Log.i("DateTimeUtils_Time", startTimeFormat.format(Date()))
            return startTimeFormat.format(Date())
        }
    }
}