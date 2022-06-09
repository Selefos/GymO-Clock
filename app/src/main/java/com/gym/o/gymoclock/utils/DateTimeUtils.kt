package com.gym.o.gymoclock.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {


    companion object {
        //private val currentDateTime: LocalDateTime = LocalDateTime.now()
        private val date: Date = Date()

        fun getCurrentYear(): String {
//        val calendar: Calendar = Calendar.getInstance()
//        Log.i("DateTimeUtils_Month", calendar.get(Calendar.YEAR).toString())
//        return calendar.get(Calendar.YEAR)
            val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            Log.i("DateTimeUtils_Year", yearFormat.format(date))
            return yearFormat.format(date)
        }


        fun getCurrentMonth(): String {
            //val monthNameFormat = DateTimeFormatter.ofPattern("MMMM")
            //return currentDateTime.format(monthNameFormat)
            val monthName = SimpleDateFormat("MMMM", Locale.getDefault())
            Log.i("DateTimeUtils_Month", monthName.format(date))
            return monthName.format(date)
        }

        fun setCalendarTableName(): String {
            Log.i(
                "DateTimeUtils_MonthYear",
                "${getCurrentMonth()} ${getCurrentYear()}".replace(" ", "_")
            )
            return "${getCurrentMonth()} ${getCurrentYear()}".replace(" ", "_")
        }


        fun getDate(): String {
//        val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
//        return currentDateTime.format(dateFormat)

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            Log.i("DateTimeUtils_Date", dateFormat.format(date))
            return dateFormat.format(date)

        }

        fun getCurrentTime(): String {
//        val startTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")
//        return currentDateTime.format(statTimeFormat)
            val startTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            Log.i("DateTimeUtils_Time", startTimeFormat.format(date))
            return startTimeFormat.format(date)
        }
    }

}