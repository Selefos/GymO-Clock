package com.gym.o.gymoclock.functionality.common.workout_db_calls

import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.functionality.main_activity_pr.*
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality.restTimeInMillis
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality.workTimeInMillis
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseElements
import com.gym.o.gymoclock.functionality.workout_pr.rounds
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils
import com.gym.o.gymoclock.utils.WidgetsWarningsUtils


fun WorkoutFragment.updateExerciseValues(dataPosition: Int) {
    val position = dataList[dataPosition]
    workoutDB = WorkoutDB(requireContext())

    if (nameIsDuplicate(dialogBuilderUtils.exerciseNameEdit.text.toString()))
        WidgetsWarningsUtils.editTextWarning(dialogBuilderUtils.exerciseNameEdit, "Exercise already registered")
    else {
        val oldExerciseName = position.exerciseNameValue

        if (dialogBuilderUtils.exerciseNameEdit.text.toString().isNotEmpty()) {
            position.exerciseNameValue = dialogBuilderUtils.exerciseNameEdit.text.toString()
            listAdapter.notifyItemChanged(dataPosition, position.exerciseNameValue)
        }

        if (dialogBuilderUtils.workDigitalTime.text.toString().isNotEmpty()) {
            position.exerciseClockValue.text = dialogBuilderUtils.workDigitalTime.text.toString()//FormatUtils.convertTimeToDigitalClock(dialogBuilderUtils.workTimePicker.text.toString())
            listAdapter.notifyItemChanged(dataPosition, position.exerciseClockValue)
        }

        if (dialogBuilderUtils.restDigitalTime.text.toString().isNotEmpty()) {
            position.restClockValue.text = dialogBuilderUtils.restDigitalTime.text.toString()//FormatUtils.convertTimeToDigitalClock(dialogBuilderUtils.restTimePicker.text.toString())
            listAdapter.notifyItemChanged(dataPosition, position.restClockValue)
        }

        saveExerciseValues("update", oldExerciseName, position.exerciseNameValue, position.exerciseClockValue, position.restClockValue)

        binding.totalTime.text = FormatUtils.convertTimeToDigitalClock((listAdapter.totalTimeFromDB(rounds)).toString())

        if (listAdapter.totalTimeFromDB(rounds) > 0) {
            binding.totalTimeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_text_color))
            binding.totalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_text_color))
        }
        loadRecyclerViews()
        dialogBuilderUtils.dialog.dismiss()
    }
}

fun WorkoutFragment.saveExerciseValues(action: String, oldExerciseName: String, exerciseName: String, exerciseClock: TextView, restClock: TextView) {

    if (action == "add") {
        val insertExerciseData = workoutDB.insertExerciseDetails(
            workoutTableName, exerciseName,
            FormatUtils.convertTimeToSeconds(exerciseClock.text.toString()).toString(),
            FormatUtils.convertTimeToSeconds(restClock.text.toString()).toString()
        )

        if (insertExerciseData)
            Toast.makeText(context, "Exercise Inserted", Toast.LENGTH_SHORT).show()
    }

    if (action == "update") {
        val updateExerciseData = workoutDB.updateSelectedExerciseDetails(
            workoutTableName, oldExerciseName, exerciseName,
            FormatUtils.convertTimeToSeconds(exerciseClock.text.toString()).toString(),
            FormatUtils.convertTimeToSeconds(restClock.text.toString()).toString()
        )
        if (updateExerciseData)
            Toast.makeText(context, "Exercise Updated", Toast.LENGTH_SHORT).show()
    }
}

var allowedExerciseNumber = 10
fun WorkoutFragment.addEditExercise() {

    if (listAdapter.itemCount >= allowedExerciseNumber) {
        Toast.makeText(context, "Exceeded permitted exercise limit", Toast.LENGTH_LONG).show()
        dialogBuilderUtils.dialog.dismiss()
        return
    }

    var exerciseName = "Exercise Name"

    if (dialogBuilderUtils.exerciseNameEdit.text.toString().isNotEmpty())
        exerciseName = dialogBuilderUtils.exerciseNameEdit.text.toString().trim()

    if (nameIsDuplicate(exerciseName))
        WidgetsWarningsUtils.editTextWarning(dialogBuilderUtils.exerciseNameEdit, "Exercise already registered")
    else {
        val exerciseClock: TextView = dialogBuilderUtils.viewRecycler.findViewById(R.id.countdown_work)
        exerciseClock.text = dialogBuilderUtils.workDigitalTime.text.toString()//FormatUtils.convertTimeToDigitalClock(dialogBuilderExercise.workTimePicker.text.toString())

        val restClock: TextView = dialogBuilderUtils.viewRecycler.findViewById(R.id.countdown_rest)
        restClock.text = dialogBuilderUtils.restDigitalTime.text.toString()//FormatUtils.convertTimeToDigitalClock(dialogBuilderExercise.restTimePicker.text.toString())

        workCountDown = object : CountDownTimer(0, 1000) {
            override fun onTick(millsUntilFinish: Long) {
                workTimeInMillis = millsUntilFinish
            }

            override fun onFinish() {}
        }
        restCountDown = object : CountDownTimer(0, 1000) {
            override fun onTick(millsUntilFinish: Long) {
                restTimeInMillis = millsUntilFinish
            }

            override fun onFinish() {}
        }

        dataList.add(
            ExerciseElements(
                exerciseName, exerciseClock, restClock, workCountDown, restCountDown,
                wTimerIsRunning = false, wTimerIsPaused = false,
                rTimerIsRunning = false, rTimerIsPaused = false
            )
        )
        saveExerciseValues("add", "", exerciseName, exerciseClock, restClock)

        binding.totalTime.text = FormatUtils.convertTimeToDigitalClock((listAdapter.totalTimeFromDB(rounds)).toString())
        loadRecyclerViews()
        dialogBuilderUtils.dialog.dismiss()
    }
}