package com.gym.o.gymoclock.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.EditText
import com.gym.o.gymoclock.databases.ExercisesScreenDB
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import java.util.*
import kotlin.collections.ArrayList

class TreeMapUtils {

    val screeningDetailsHashMap: SortedMap<String, ArrayList<ArrayList<String>>> = TreeMap()
    val roundWeightEditTexts = arrayListOf<EditText>()
    val roundRepsEditTexts = arrayListOf<EditText>()
    val exerciseNameStringList = arrayListOf<String>()
    var roundWeightStringList = arrayListOf<String>()
    var roundRepsStringList = arrayListOf<String>()
    private var detailsArray = arrayListOf<ArrayList<String>>()

    fun loadCurrentWorkoutExercises(context: Context): ArrayList<String> {
        val workoutDB = WorkoutDB(context)
        val db: SQLiteDatabase = workoutDB.readableDatabase
        val cursorWorkoutDB = workoutDB.loadRecyclerElements(workoutTableName, db)
        val exerciseNameList = arrayListOf<String>()

        if (cursorWorkoutDB.moveToFirst()) {
            do {
                exerciseNameList.add(cursorWorkoutDB.getString(1))
            } while (cursorWorkoutDB.moveToNext())
        }
        cursorWorkoutDB.close()
        db.close()

        return exerciseNameList
    }

    fun loadScreenedWorkoutExercises(context: Context, tableName: String, workoutID: String): ArrayList<String> {
        val exercisesScreenDB = ExercisesScreenDB(context)
        val db: SQLiteDatabase = exercisesScreenDB.readableDatabase
        val exerciseNameList = arrayListOf<String>()
        val list = exercisesScreenDB.list(tableName, workoutID)
        val arrayList = FormatUtils.convertStringToArray(list)

        for (i in arrayList.indices)
            exerciseNameList.add(arrayList[i])

        db.close()
        return exerciseNameList
    }

    fun isHashMapPrepared(): Boolean {
        var isEditTextReady = true
        for (i in roundWeightEditTexts.indices) {
            Log.i("exerciseNameStringList_hash", exerciseNameStringList[i])
            if (ErrorUtils.isTextEmpty(roundWeightEditTexts[i]).isEmpty()) {
                isEditTextReady = false
                //return false
            } else {
                roundWeightStringList.add(roundWeightEditTexts[i].text.toString())
                Log.i("roundWeightStringList_hash", roundWeightStringList[i])
            }
            if (ErrorUtils.isTextEmpty(roundRepsEditTexts[i]).isEmpty()) {
                isEditTextReady = false
                //return false
            } else {
                roundRepsStringList.add(roundRepsEditTexts[i].text.toString())
                Log.i("roundRepsStringList_hash", roundRepsStringList[i])
            }
        }

        return isEditTextReady
    }

    fun addToTreeMap(sets: Int): ArrayList<ArrayList<String>>{
        detailsArray.add(exerciseNameStringList)
        detailsArray.add(roundWeightStringList)
        detailsArray.add(roundRepsStringList)
        screeningDetailsHashMap["Round $sets"] = detailsArray
        return detailsArray
    }

    fun instantiateLists() {
        exerciseNameStringList.clear()
        roundWeightEditTexts.clear()
        roundRepsEditTexts.clear()
        roundWeightStringList = ArrayList()
        roundRepsStringList = ArrayList()
        detailsArray = ArrayList()
    }

}