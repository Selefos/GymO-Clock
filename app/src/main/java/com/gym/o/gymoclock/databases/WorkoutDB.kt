package com.gym.o.gymoclock.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WorkoutDB(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT")
        onCreate(db)
        db.close()
    }

    fun addWorkoutTable(workoutName: String) {
        val db = writableDatabase
        val createWorkoutTable = "CREATE TABLE IF NOT EXISTS " + workoutName + " (ID INTEGER PRIMARY KEY , " +
                "$COL_EXERCISE_NAME TEXT, $COL_WORK_TIME INTEGER, $COL_REST_TIME INTEGER)"
        db.execSQL(createWorkoutTable)

        db.execSQL("INSERT INTO $workoutName ($COL_EXERCISE_NAME, $COL_WORK_TIME, $COL_REST_TIME) VALUES ('Exercise One', '5', '3')")
        db.execSQL("INSERT INTO $workoutName ($COL_EXERCISE_NAME, $COL_WORK_TIME, $COL_REST_TIME) VALUES ('Exercise Two', '5', '3')")
        db.execSQL("INSERT INTO $workoutName ($COL_EXERCISE_NAME, $COL_WORK_TIME, $COL_REST_TIME) VALUES ('Exercise Three', '5', '3')")
        db.close()
    }

    fun renameWorkoutTable(workoutName: String, WorkoutNameUpdate: String) {
        val db = this.writableDatabase
        val updateTableWorkoutName = "ALTER TABLE $workoutName RENAME TO $WorkoutNameUpdate"
        db.execSQL(updateTableWorkoutName)
        db.close()
    }

    fun deleteWorkoutTable(workoutName: String) {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $workoutName")
        db.close()
    }

    fun loadWorkoutTableNames(): List<String> {
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

    fun loadLastWorkoutTable(): String {
        val db = this.writableDatabase
        val selectTables = "SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata'"
        val cursor = db.rawQuery(selectTables, null)
        var result = ""
        if (cursor.moveToLast()) {
            result = cursor.getString(0)
        }

        return result
    }

    fun insertExerciseDetails(workoutName: String, exerciseName: String, workTime: String, restTime: String): Boolean {
        val db = this.writableDatabase
        val exerciseDetails = ContentValues()
        exerciseDetails.put(COL_EXERCISE_NAME, exerciseName)
        exerciseDetails.put(COL_WORK_TIME, workTime)
        exerciseDetails.put(COL_REST_TIME, restTime)
        val result: Long = db.insert(workoutName, null, exerciseDetails)
        db.close()
        return result != -1L
    }

    fun updateExerciseDetails(workoutName: String, nameToReplace: String, newExerciseName: String, workTime: String, restTime: String): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val exerciseDetails = ContentValues()
        exerciseDetails.put(COL_EXERCISE_NAME, newExerciseName)
        exerciseDetails.put(COL_WORK_TIME, workTime)
        exerciseDetails.put(COL_REST_TIME, restTime)
        val result = db.update(workoutName, exerciseDetails, "$COL_EXERCISE_NAME = ?", arrayOf(nameToReplace))
        db.close()
        return result != 1
    }

    fun deleteWorkoutDetails(workoutName: String, exerciseName: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(workoutName, "$COL_EXERCISE_NAME = ?", arrayOf(exerciseName))
        db.close()
        return result != -1
    }

    fun loadRecyclerElements(workoutName: String, db: SQLiteDatabase): Cursor {
        return db.query(workoutName, null, null, null, null, null, null)
    }

    fun checkForDuplicateNames(workoutName: String): List<String> {
        /*------------------- SELECT ALL QUERIES -------------------*/
        val list: MutableList<String> = ArrayList()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $workoutName"
        val cursor = db.rawQuery(selectQuery, null)

        /*-------- Loops through all rows and adds to list ---------*/
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1))
            } while (cursor.moveToNext())
        }
        /*-------------------- Close Connection --------------------*/
        cursor.close()
        db.close()
        return list
    }

    fun totalTimeFromWorkoutDB(workoutName: String, rounds: Int): String {
        val db = writableDatabase

        val cursor = db.rawQuery("SELECT SUM($COL_WORK_TIME) + SUM($COL_REST_TIME) AS TOTAL FROM $workoutName", null)
        var total = 0
        if (cursor.moveToFirst()) {
            do {
                total += cursor.getInt(cursor.getColumnIndex("TOTAL"))
            } while (cursor.moveToNext())
        }


        cursor.close()
        db.close()

        val sum = (total * rounds) - lastRestTime(workoutName)
        return sum.toString()
    }

    fun lastRestTime(workoutName: String): Int {
        //db.query(workoutName, null, null, null, null, null, "$COL_REST_TIME DESC", "1");//
        val db = this.readableDatabase
        val selectQuery = "SELECT $COL_REST_TIME FROM $workoutName WHERE ID = (SELECT MAX(ID) FROM $workoutName)"
        val cursorLastRestTime = db.rawQuery(selectQuery, null)
        var lastRestTime = 0
        if (cursorLastRestTime.moveToLast()) {
            lastRestTime = cursorLastRestTime.getInt(cursorLastRestTime.getColumnIndex(COL_REST_TIME))
        }

        cursorLastRestTime.close()
        db.close()

        return lastRestTime
    }

    companion object {
        const val DATABASE_NAME = "Workout.db"
        var TABLE_WORKOUT = "Workout_Name"
        const val COL_ID = "ID"
        const val COL_EXERCISE_NAME = "EXERCISE_NAME"
        const val COL_WORK_TIME = "WORK_TIME"
        const val COL_REST_TIME = "REST_TIME"
    }
}