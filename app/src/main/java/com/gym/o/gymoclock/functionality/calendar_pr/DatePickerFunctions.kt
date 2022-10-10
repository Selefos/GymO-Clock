package com.gym.o.gymoclock.functionality.calendar_pr

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databases.ExercisesScreenDB
import com.gym.o.gymoclock.ui.calendar.CalendarFragment
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.DialogBuilderUtils
import com.gym.o.gymoclock.utils.ErrorUtils
import com.gym.o.gymoclock.utils.FormatUtils
import java.time.Month


fun CalendarFragment.onDateChangeListener() {

    binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
        val setMonth = month + 1

        val date = String.format("%02d-%02d-%02d", dayOfMonth, setMonth, year)
        val tableName = "${Month.of(setMonth)}_$year"
        Log.d("TAG", "onDateSet: mm/dd/yyy: $date ${DateTimeUtils.setCalendarTableName()} $tableName")

        binding.buttonPopLayout.removeAllViews()
        if (ErrorUtils.tableNameExists(tableName, requireContext()))
            showWorkoutDetails(tableName, date)
    }

}

fun CalendarFragment.showWorkoutDetails(tableName: String, date: String) {

    val calendarDB = CalendarDB(context)
    val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
    val cursor: Cursor = calendarDB.getCalendarWorkoutID(tableName, date, sqlDB)

    if (cursor.moveToFirst()) {
        do {
            createButton(tableName, cursor.getString(0), FormatUtils.stringUnderscoreToSpace(cursor.getString(1)), date)
        } while (cursor.moveToNext())
    }
    cursor.close()
    sqlDB.close()

}

fun CalendarFragment.createButton(tableName: String, workoutID: String, workoutName: String, date: String) {

    val button = Button(context)
    button.background = AppCompatResources.getDrawable(requireContext(), R.drawable.buttons_background)
    button.text = workoutName
    button.setTextColor(Color.WHITE)

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
                dialogBuilderUtils.calendarTotalTime.text = FormatUtils.calendarTotalTimeToDigitalFormat(cursor.getString(4))
                dialogBuilderUtils.calendarTotalWorkingTime.text = FormatUtils.calendarTotalTimeToDigitalFormat(cursor.getString(5))
            } while (cursor.moveToNext())
        }
        cursor.close()
        sqlDB.close()

        val bookName = date + "_" + workoutID
        dialogBuilderUtils.onClickListenerCalendarScope(bookName, tableName)

        dialogBuilderUtils.calendarWorkoutDetailsDialog(true)
    }

}