package com.gym.o.gymoclock.functionality.calendar_pr

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databases.ExercisesScreenDB
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import com.gym.o.gymoclock.ui.calendar.CalendarFragment
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.DialogBuilderUtils
import com.gym.o.gymoclock.utils.ErrorUtils
import com.gym.o.gymoclock.utils.FormatUtils
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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

        var sets: Int = 1
        if (cursor.moveToFirst()) {
            do {
                dialogBuilderUtils.calendarDate.text = cursor.getString(0)
                dialogBuilderUtils.calendarStartTime.text = cursor.getString(1)
                dialogBuilderUtils.calendarEndTime.text = cursor.getString(2)
                dialogBuilderUtils.calendarWorkoutName.text = cursor.getString(3).replace("_", " ")
                dialogBuilderUtils.calendarTotalTime.text = FormatUtils.calendarTotalTimeToDigitalFormat(cursor.getString(4))
                dialogBuilderUtils.calendarTotalWorkingTime.text = FormatUtils.calendarTotalTimeToDigitalFormat(cursor.getString(5))
                sets = cursor.getString(6).toInt()
            } while (cursor.moveToNext())
        }
        cursor.close()
        sqlDB.close()

        val bookName = date + "_" + workoutID

        dialogBuilderUtils.onClickListenerCalendarScope(bookName, tableName, workoutName, workoutID, sets)

        dialogBuilderUtils.calendarWorkoutDetailsDialog(true)
    }

}


val screeningDetailsHashMap: SortedMap<String, ArrayList<ArrayList<String>>> = TreeMap()
var roundsCountText = 1
val roundWeightEditTexts = arrayListOf<EditText>()
val roundRepsEditTexts = arrayListOf<EditText>()
val exerciseNameStringList = arrayListOf<String>()
var roundWeightStringList = arrayListOf<String>()
var roundRepsStringList = arrayListOf<String>()
var detailsArray = arrayListOf<ArrayList<String>>()

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

        if (ErrorUtils.isTextEmpty(roundWeightEditTexts[i]).isEmpty()) {
            isEditTextReady = false
        } else
            roundWeightStringList.add(roundWeightEditTexts[i].text.toString())

        if (ErrorUtils.isTextEmpty(roundRepsEditTexts[i]).isEmpty()) {
            isEditTextReady = false
        } else
            roundRepsStringList.add(roundRepsEditTexts[i].text.toString())
    }

    detailsArray.add(exerciseNameStringList)
    detailsArray.add(roundWeightStringList)
    detailsArray.add(roundRepsStringList)
    screeningDetailsHashMap["Round $roundsCountText"] = detailsArray

    return isEditTextReady
}

fun saveHashMapDetailsOnEndWorkout(context: Context, bookName: Any?) {
    val calendarDB = CalendarDB(context)
    val sqlDB: SQLiteDatabase = calendarDB.readableDatabase
    val tableName = "${(DateTimeUtils.getCurrentMonth())}_${DateTimeUtils.getCurrentYear()}"

    val cursor: Cursor = calendarDB.getCalendarWorkoutID(tableName, DateTimeUtils.getDate(), sqlDB)

    val exercisesScreenDB = ExercisesScreenDB(context)
    if (bookName == null) {
        if (cursor.moveToLast())
            exercisesScreenDB.saveData("${DateTimeUtils.getDate()}_${cursor.getString(0)}", screeningDetailsHashMap)
    } else
        exercisesScreenDB.saveData(bookName as String, screeningDetailsHashMap)

    cursor.close()
    sqlDB.close()
}

fun instantiateLists() {
    exerciseNameStringList.clear()
    roundWeightEditTexts.clear()
    roundRepsEditTexts.clear()
    roundWeightStringList = ArrayList()
    roundRepsStringList = ArrayList()
    detailsArray = ArrayList()
}