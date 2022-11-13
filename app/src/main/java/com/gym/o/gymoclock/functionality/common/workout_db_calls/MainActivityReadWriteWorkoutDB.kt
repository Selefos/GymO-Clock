package com.gym.o.gymoclock.functionality.common.workout_db_calls

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.functionality.main_activity_pr.*
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.ErrorUtils
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils

private lateinit var dialogBuilder: AlertDialog.Builder
private lateinit var dialog: AlertDialog
fun MainActivity.addWorkoutTable() {
    dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
    val inflater = layoutInflater//LayoutInflater.from(requireContext())
    val view = inflater.inflate(R.layout.dialog_add_new_workout, null)
    val workoutNameAdd = view.findViewById<EditText>(R.id.workout_name)
    val addButton = view.findViewById<Button>(R.id.add_workout_button)
    val cancelButton = view.findViewById<Button>(R.id.cancel_add_workout_button)

    dialogBuilder.setView(view)
    dialog = dialogBuilder.create()
    dialog.show()

    addButton.setOnClickListener {
        if (ErrorUtils.isTextEmpty(workoutNameAdd).isNotEmpty() && ErrorUtils.isNotDuplicate(workoutNameAdd, workoutDB).isNotEmpty()
            && ErrorUtils.isNotTextNumberOrSymbol(workoutNameAdd).isNotEmpty()) {

            workoutDB.addWorkoutTable(FormatUtils.stringSpaceToUnderscore(workoutNameAdd.text.toString()))

            SharedPreferencesUtils.saveWorkoutTableNameToPreferences(this, workoutNameAdd.text.toString())

            changeFragment(WorkoutFragment::class.java)

            changeNavHeaderText(SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this))
            prepareMenuData()
            expandableAdapter.notifyDataSetChanged()

            dialog.dismiss()
        }
    }

    cancelButton.setOnClickListener { dialog.dismiss() }

}


fun MainActivity.editWorkoutName(workoutName: String) {
    dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
    val inflater = layoutInflater//LayoutInflater.from(requireContext())
    val view = inflater.inflate(R.layout.dialog_delete_rename_workout, null)
    val editIndicator = view.findViewById<TextView>(R.id.edit_indicator)
    val renameWorkout = view.findViewById<EditText>(R.id.rename_text_workout)
    val updateWorkoutButton = view.findViewById<Button>(R.id.rename_workout)
    val deleteWorkoutButton = view.findViewById<Button>(R.id.delete_workout)

    val updateTextView: String = getString(R.string.edit_indicator) + " " + FormatUtils.stringUnderscoreToSpace(workoutName)
    editIndicator.text = updateTextView

    val updateEditTextHint: String = getString(R.string.rename) + " \"" + FormatUtils.stringUnderscoreToSpace(workoutName) + "\""
    renameWorkout.hint = updateEditTextHint

    dialogBuilder.setView(view)
    dialog = dialogBuilder.create()
    dialog.show()

    updateWorkoutButton.setOnClickListener {
        if (ErrorUtils.isTextEmpty(renameWorkout).isNotEmpty() && ErrorUtils.isNotDuplicate(renameWorkout, workoutDB).isNotEmpty()
            && ErrorUtils.isNotTextNumberOrSymbol(renameWorkout).isNotEmpty()) {

            workoutDB.renameWorkoutTable(workoutName, FormatUtils.stringSpaceToUnderscore(renameWorkout.text.toString()))

            SharedPreferencesUtils.saveWorkoutTableNameToPreferences(this, renameWorkout.text.toString())
            changeFragment(WorkoutFragment::class.java)

            changeNavHeaderText(SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this))
            prepareMenuData()
            expandableAdapter.notifyDataSetChanged()

            dialog.dismiss()
        }
    }

    deleteWorkoutButton.setOnClickListener {
        workoutDB.deleteWorkoutTable(FormatUtils.stringSpaceToUnderscore(workoutName))

        //setWorkoutTableName(workoutDB.loadLastWorkoutTable())
        SharedPreferencesUtils.saveWorkoutTableNameToPreferences(this, FormatUtils.stringSpaceToUnderscore(workoutDB.loadLastWorkoutTable()))
        changeFragment(WorkoutFragment::class.java)

        changeNavHeaderText(SharedPreferencesUtils.getWorkoutTableNameFromPreferences(this))
        prepareMenuData()
        expandableAdapter.notifyDataSetChanged()

        dialog.dismiss()
    }
}


fun MainActivity.setWorkoutTableName(workoutName: String) {
    workoutTableName = workoutName
    SharedPreferencesUtils.saveWorkoutTableNameToPreferences(this, workoutTableName)
}