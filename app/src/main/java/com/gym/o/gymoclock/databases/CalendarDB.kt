package com.gym.o.gymoclock.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.gym.o.gymoclock.utils.DateTimeUtils

class CalendarDB(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALENDAR")
        onCreate(db)
        db.close()
    }

    fun addCalendarTable(monthYearTable: String) {
        val db = writableDatabase
        val createCalendarTable = "CREATE TABLE IF NOT EXISTS $monthYearTable (ID INTEGER PRIMARY KEY , $COL_DATE DATE , $COL_START_TIME TEXT, $COL_END_TIME TEXT, $COL_WORKOUT_NAME TEXT, $COL_TOTAL_TIME TEXT, $COL_TOTAL_WORKING_TIME TEXT)"
        db.execSQL(createCalendarTable)
        db.close()
    }

    fun loadCalendarTableNames(): List<String> {
        val db = this.writableDatabase
        val selectTables = "SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata'"
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

    fun loadCalendarDetails(tableName: String, id: String, sqlDB: SQLiteDatabase): Cursor {
        val getData = arrayOf(COL_DATE, COL_START_TIME, COL_END_TIME, COL_WORKOUT_NAME, COL_TOTAL_TIME, COL_TOTAL_WORKING_TIME)
        val select = "$COL_ID LIKE ?"
        val selection = arrayOf(id)
        return sqlDB.query(tableName, getData, select, selection, null, null, null)
    }

    fun getCalendarWorkoutID(tableName: String, date: String, sqlDB: SQLiteDatabase): Cursor {
        val getData = arrayOf(COL_ID, COL_WORKOUT_NAME)
        val select = "$COL_DATE LIKE ?"
        val selection = arrayOf(date)
        return sqlDB.query(tableName, getData, select, selection, null, null, null)
    }

    fun getCalendarWorkoutDetailsForScreening(tableName: String, date: String, sqlDB: SQLiteDatabase): Cursor {
        val getData = arrayOf(COL_ID, COL_WORKOUT_NAME)
        val select = "$COL_DATE LIKE ?"
        val selection = arrayOf(date)
        return sqlDB.query(tableName, getData, select, selection, null, null, null)
    }

    fun getCalendarWorkoutDate(tableName: String, workoutName: String, sqlDB: SQLiteDatabase): Cursor {
        val getData = arrayOf(COL_ID, COL_DATE, COL_WORKOUT_NAME)
        val select = "$COL_WORKOUT_NAME LIKE ?"
        val selection = arrayOf(workoutName)
        return sqlDB.query(tableName, getData, select, selection, null, null, null)
    }

    fun insertCalendarDetails(monthYearTable: String, date: String, startTime: String, endTime: String, workoutName: String, totalTime: String, totalWorkingTime: String): Boolean {
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
        const val DATABASE_NAME = "Calendar.db"

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