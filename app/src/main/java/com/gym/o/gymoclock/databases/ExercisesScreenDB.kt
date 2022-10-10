package com.gym.o.gymoclock.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.view.View
import android.widget.ListView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.functionality.calendar_pr.ExerciseScreeningAdapter
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseScreening
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.DateTimeUtils
import io.paperdb.Paper


class ExercisesScreenDB(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    private var context: Context? = null

    init {
        this.context = context
    }

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

    fun list(storedId: String): String {
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

    fun saveData(bookName: String, screeningDetailsHashMap: HashMap<String, ArrayList<ArrayList<String>>>) {
        val dbDataMap: HashMap<String, ArrayList<ArrayList<String>>> = screeningDetailsHashMap
        Paper.book(DateTimeUtils.getCurrentMonth() + "_" + DateTimeUtils.getCurrentYear()).write(bookName, dbDataMap)
    }

    fun readDataList(bookName: String, keyName: String, viewSpecifyDetailsLayout: View) {

        val listView = viewSpecifyDetailsLayout.findViewById<ListView>(R.id.exercise_details_list)
        val data = Paper.book(keyName).read<HashMap<String, ArrayList<ArrayList<String>>>>(bookName)
        val keysList: ArrayList<ExerciseScreening> = ArrayList()

        var exerciseName: String

        for (index in 0 until data?.get("Round1")!![0].size - 1) {
            exerciseName = data["Round1"]!![0][index + 1]
            keysList.add(ExerciseScreening(exerciseName, index))
        }

        val exerciseScreeningAdapter = ExerciseScreeningAdapter(context!!, R.layout.dialog_exercise_screen_list_layout, keysList, bookName, keyName)
        listView.adapter = exerciseScreeningAdapter

    }


}