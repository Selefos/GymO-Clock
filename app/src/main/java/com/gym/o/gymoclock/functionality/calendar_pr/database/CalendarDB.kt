package com.gym.o.gymoclock.functionality.calendar_pr.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.gym.o.gymoclock.utils.DateTimeUtils
import kotlin.collections.ArrayList

class CalendarDB (context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {


    override fun onCreate(db: SQLiteDatabase) {}

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALENDAR")
        onCreate(db)
        db.close()
    }

    fun addCalendarTable(monthYearTable: String) {
        val db = writableDatabase
        val createCalendarTable = "CREATE TABLE IF NOT EXISTS " + monthYearTable + " (ID INTEGER PRIMARY KEY , " +
                "$COL_DATE DATE , $COL_START_TIME INTEGER, $COL_END_TIME INTEGER, $COL_WORKOUT_NAME TEXT, $COL_TOTAL_TIME INTEGER, $COL_TOTAL_WORKING_TIME INTEGER)"
        db.execSQL(createCalendarTable)
        db.close()
    }

    fun renameCalendarTable(tableName: String, WorkoutNameUpdate: String) {
        val db = this.writableDatabase
        val updateTableWorkoutName =
            "ALTER TABLE $tableName RENAME TO $WorkoutNameUpdate"
        db.execSQL(updateTableWorkoutName)
        db.close()
    }

    fun deleteCalendarTable(tableName: String) {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $tableName")
    }

    fun loadCalendarTableNames(): List<String> {
        val db = this.writableDatabase
        val selectTables =
            "SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata'"
        val cursor = db.rawQuery(selectTables, null)
        val result: MutableList<String> = ArrayList()
        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return result
    }

    fun insertCalendarDetails(monthYearTable: String, date: String, startTime: String, endTime: String,workoutName: String, totalTime: String, totalWorkingTime: String): Boolean{
        val db = this.writableDatabase
        val calendarDetails = ContentValues()
        calendarDetails.put(COL_DATE, date)
        calendarDetails.put(COL_START_TIME, startTime)
        calendarDetails.put(COL_END_TIME, endTime)
        calendarDetails.put(COL_WORKOUT_NAME, workoutName)
        calendarDetails.put(COL_TOTAL_TIME, totalTime)
        calendarDetails.put(COL_TOTAL_WORKING_TIME, totalWorkingTime)
        val result: Long = db.insert(monthYearTable, null, calendarDetails)
        db.close()
        return result != -1L
    }


    companion object {

        @RequiresApi(Build.VERSION_CODES.Q)
        val dateTimeUtils: DateTimeUtils = DateTimeUtils()

        const val DATABASE_NAME = "Calendar.db"
        @RequiresApi(Build.VERSION_CODES.Q)
        var TABLE_CALENDAR = DateTimeUtils.setCalendarTableName()
        const val COL_ID = "ID"
        const val COL_DATE = "DATE"
        const val COL_START_TIME = "START_TIME"
        const val COL_END_TIME = "END_TIME"
        const val COL_WORKOUT_NAME = "WORKOUT_NAME"
        const val COL_TOTAL_TIME = "TOTAL_TIME"
        const val COL_TOTAL_WORKING_TIME = "TOTAL_WORKING_TIME"
    }
}