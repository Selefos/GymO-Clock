package com.gym.o.gymoclock.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//Saves Screen of Exercises done!

class ExercisesScreenDB(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME, null, 1){

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE IF NOT EXISTS $TABLE_SCREEN_EXERCISES (ID INTEGER PRIMARY KEY , $COL_STORED_ID INTEGER, $COL_DATE DATE, $COL_WORKOUT_NAME ΤΕΧΤ, $COL_EXERCISES_LIST TEXT)"
        db!!.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SCREEN_EXERCISES")
        onCreate(db)
    }

    fun insertScreenDBDetails(id: String, date: String, workoutName: String, exercisesList: String): Boolean {
        val db = this.writableDatabase
        val screeningDetails = ContentValues()
        screeningDetails.put(COL_STORED_ID, id)
        screeningDetails.put(COL_DATE, date)
        screeningDetails.put(COL_WORKOUT_NAME, workoutName)
        screeningDetails.put(COL_EXERCISES_LIST, exercisesList)
        val result: Long = db.insert(TABLE_SCREEN_EXERCISES, null, screeningDetails)
        db.close()
        return result != -1L
    }

    fun getScreenDBList(id: String, db: SQLiteDatabase): Cursor {
        val getData = arrayOf(CalendarDB.COL_ID, CalendarDB.COL_WORKOUT_NAME)
        val select = "$COL_STORED_ID LIKE ?"
        val selection = arrayOf(id)
        return db.query(TABLE_SCREEN_EXERCISES, getData, select, selection, null, null, null)
    }

    fun list(storedId: String): String{
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_EXERCISES_LIST FROM $TABLE_SCREEN_EXERCISES WHERE $COL_STORED_ID = $storedId", null)
        var list = ""
        if (cursor.moveToFirst()) {
            do {
                list = cursor.getString(0)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    companion object {
        const val DATABASE_NAME = "ExercisesScreen.db"

        var TABLE_SCREEN_EXERCISES = "SCREEN_EXERCISES"
        const val COL_STORED_ID = "STORED_ID"
        const val COL_WORKOUT_NAME = "WORKOUT_NAME"
        const val COL_DATE = "DATE"
        const val COL_EXERCISES_LIST = "EXERCISES_LIST"
    }
}