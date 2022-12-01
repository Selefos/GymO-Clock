package com.gym.o.gymoclock.testFunctions

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databases.ExercisesScreenDB

fun MainActivity.changeDataCalendar() {
    val calendarDB = CalendarDB(this)
    val t0 = "SEPTEMBER_2022"
    val sqlDB: SQLiteDatabase = calendarDB.readableDatabase

    val cursor: Cursor = getCalendarTimes(t0, sqlDB)

    if (cursor.moveToFirst()) {
        do {
            println(cursor.getString(0))
            println("00:${cursor.getString(1)}")
            calendarDB.updateSelectedCalendarDetails(t0, cursor.getString(0),
                "00:${cursor.getString(1)}", "00:${cursor.getString(2)}")

        } while (cursor.moveToNext())
    }
    cursor.close()
    sqlDB.close()
}

fun CalendarDB.calendarDBDeleteID(tableName: String, id: String): Boolean {
    val db = this.writableDatabase
    val result = db.delete(tableName, "${CalendarDB.COL_ID} = ?", arrayOf(id))
    db.close()
    return result != -1
}

fun ExercisesScreenDB.screenDBDeleteID(tableName: String, id: String): Boolean {
    val db = this.writableDatabase
    val result = db.delete(tableName, "${ExercisesScreenDB.COL_STORED_ID} = ?", arrayOf(id))
    db.close()
    return result != -1
}

fun CalendarDB.updateSelectedCalendarDetails(tableName: String, id: String, totalTime: String, totalWorkingTime: String): Boolean {
    val db: SQLiteDatabase = this.writableDatabase
    val exerciseDetails = ContentValues()
    exerciseDetails.put(CalendarDB.COL_TOTAL_TIME, totalTime)
    exerciseDetails.put(CalendarDB.COL_TOTAL_WORKING_TIME, totalWorkingTime)
    val result = db.update(tableName, exerciseDetails, "${CalendarDB.COL_ID} = ?", arrayOf(id))
    db.close()
    return result != 1
}

fun getCalendarTimes(tableName: String, sqlDB: SQLiteDatabase): Cursor {
    val getData = arrayOf(CalendarDB.COL_ID, CalendarDB.COL_TOTAL_TIME, CalendarDB.COL_TOTAL_WORKING_TIME)
    return sqlDB.query(tableName, getData, null, null, null, null, null)
}

fun CalendarDB.calendarDBDeleteTestEntries(tableName: String, exerciseName: String): Boolean {
    val db = this.writableDatabase
    val result = db.delete(tableName, "${CalendarDB.COL_WORKOUT_NAME} = ?", arrayOf(exerciseName))
    db.close()
    return result != -1
}

fun ExercisesScreenDB.screenDBDeleteTestEntries(tableName: String, exerciseName: String): Boolean {
    val db = this.writableDatabase
    val result = db.delete(tableName, "${ExercisesScreenDB.COL_WORKOUT_NAME} = ?", arrayOf(exerciseName))
    db.close()
    return result != -1
}