package com.gym.o.gymoclock.functionality.workout_pr.database

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

    fun addWorkoutTable(WorkoutName: String) {
        val db = writableDatabase
        val createWorkoutTable =
            "CREATE TABLE IF NOT EXISTS " + WorkoutName + " (ID INTEGER PRIMARY KEY , " +
                    "$COL_EXERCISE_NAME TEXT, $COL_WORK_TIME INTEGER, $COL_REST_TIME INTEGER)"
        db.execSQL(createWorkoutTable)

        db.execSQL("INSERT INTO $WorkoutName ($COL_EXERCISE_NAME, $COL_WORK_TIME, $COL_REST_TIME) VALUES ('Exercise One', '0', '0')")
        db.execSQL("INSERT INTO $WorkoutName ($COL_EXERCISE_NAME, $COL_WORK_TIME, $COL_REST_TIME) VALUES ('Exercise Two', '0', '0')")
        db.execSQL("INSERT INTO $WorkoutName ($COL_EXERCISE_NAME, $COL_WORK_TIME, $COL_REST_TIME) VALUES ('Exercise Three', '0', '0')")

        db.close()
    }

    fun renameWorkoutTable(tableName: String, WorkoutNameUpdate: String) {
        val db = this.writableDatabase
        val updateTableWorkoutName =
            "ALTER TABLE $tableName RENAME TO $WorkoutNameUpdate"
        db.execSQL(updateTableWorkoutName)
        db.close()
    }

    fun deleteWorkoutTable(tableName: String) {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $tableName")
        db.close()
    }

    fun loadWorkoutTableNames(): List<String> {
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

    fun insertExerciseDetails(tableName: String, exerciseName: String, workTime: String, restTime: String): Boolean {
        val db = this.writableDatabase
        val exerciseDetails = ContentValues()
        exerciseDetails.put(COL_EXERCISE_NAME, exerciseName)
        exerciseDetails.put(COL_WORK_TIME, workTime)
        exerciseDetails.put(COL_REST_TIME, restTime)
        val result: Long = db.insert(tableName, null, exerciseDetails)
        db.close()
        return result != -1L
    }

    fun updateExerciseDetails(tableName: String, nameToReplace: String, newExerciseName: String, workTime: String, restTime: String): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val exerciseDetails = ContentValues()
        exerciseDetails.put(COL_EXERCISE_NAME, newExerciseName)
        exerciseDetails.put(COL_WORK_TIME, workTime)
        exerciseDetails.put(COL_REST_TIME, restTime)
        val result =
            db.update(tableName, exerciseDetails, "$COL_EXERCISE_NAME = ?", arrayOf(nameToReplace))
        db.close()
        return result != 1
    }

    fun deleteWorkoutDetails(tableName: String, exerciseName: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(tableName, "EXERCISE_NAME = ?", arrayOf(exerciseName))
        db.close()
        return result != -1
    }

    fun loadRecyclerElements(tableName: String, db: SQLiteDatabase): Cursor {

        return db.query(tableName, null, null, null, null, null, null)
    }

    fun checkForDuplicateNames(TableName: String): List<String> {
        /*------------------- SELECT ALL QUERIES -------------------*/
        val list: MutableList<String> = ArrayList()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TableName"
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

    companion object {
        const val DATABASE_NAME = "Workout.db"
        var TABLE_WORKOUT = "Workout_Name"
        const val COL_ID = "ID"
        const val COL_EXERCISE_NAME = "EXERCISE_NAME"
        const val COL_WORK_TIME = "WORK_TIME"
        const val COL_REST_TIME = "REST_TIME"
    }
}