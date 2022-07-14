package com.gym.o.gymoclock.functionality.calendar_pr

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.ui.calendar.CalendarFragment
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.DialogBuilderUtils
import java.time.Month


fun CalendarFragment.onDateChangeListener() {

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val month = month + 1


            val date = String.format("%02d-%02d-%02d", dayOfMonth, month, year)
            val tableName = "${Month.of(month)}_$year"
            Log.d("TAG", "onDateSet: mm/dd/yyy: $date ${DateTimeUtils.setCalendarTableName()}")

            binding.buttonPopLayout.removeAllViews()
            if(tableNameExists(tableName))
                showWorkoutDetails(tableName, date)
        }

    }

    private fun CalendarFragment.showWorkoutDetails(tableName: String, date: String) {

        val calendarDB = CalendarDB(context)
        val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
        val cursor: Cursor = calendarDB.getCalendarWorkoutID(tableName, date, sqlDB)

        if (cursor.moveToFirst()) {
            do {
                createButton(tableName, cursor.getString(0), cursor.getString(1).replace("_", " "))
            } while (cursor.moveToNext())
        }
        cursor.close()
        sqlDB.close()

    }

    private fun CalendarFragment.createButton(tableName: String, workoutID: String, workoutName: String) {

        val button = Button(context)
        button.background = AppCompatResources.getDrawable(requireContext(), R.drawable.buttons_background)
        button.text = workoutName
        button.setTextColor(Color.WHITE)

        //button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        //binding.buttonPopLayout.addView(button)

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 0, 20)
        binding.buttonPopLayout.addView(button, layoutParams)

        button.setOnClickListener {

            val calendarDB = CalendarDB(context)
            val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
            val cursor: Cursor = calendarDB.loadCalendarDetails(tableName, workoutID, sqlDB)
            val dialogBuilderUtils = DialogBuilderUtils(requireContext())

            if (cursor.moveToFirst()) {
                do {
                    dialogBuilderUtils.calendarDate.text = cursor.getString(0)
                    dialogBuilderUtils.calendarStartTime.text = cursor.getString(1)
                    dialogBuilderUtils.calendarEndTime.text = cursor.getString(2)
                    dialogBuilderUtils.calendarWorkoutName.text = cursor.getString(3).replace("_", " ")
                    dialogBuilderUtils.calendarTotalTime.text = FormatUtils.convertTimeToDigitalClockMinSecFormat(cursor.getString(4))
                    dialogBuilderUtils.calendarTotalWorkingTime.text = FormatUtils.convertTimeToDigitalClockMinSecFormat(cursor.getString(5))
                } while (cursor.moveToNext())
            }
            cursor.close()
            sqlDB.close()

            dialogBuilderUtils.calendarWorkoutDetails(true)

        }

    }

fun CalendarFragment.tableNameExists(tableName: String): Boolean {

    val calendarDB = CalendarDB(context)
    val isExisting = calendarDB.loadCalendarTableNames()
    for (name in isExisting) {
        if (tableName == name.uppercase()) {
            return true
        }
    }
    return false
}

