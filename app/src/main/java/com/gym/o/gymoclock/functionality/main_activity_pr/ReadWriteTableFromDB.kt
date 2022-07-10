package com.gym.o.gymoclock.functionality.main_activity_pr

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import com.gym.o.gymoclock.ui.workout.WorkoutFragment

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
        if (isTextEmpty(workoutNameAdd).isNotEmpty() && isNotDuplicate(workoutNameAdd, workoutDB).isNotEmpty()
            && isNotTextNumberOrSymbol(workoutNameAdd).isNotEmpty()) {

            workoutDB.addWorkoutTable(workoutNameAdd.text.toString())

            sharedPreferencesUtils.saveWorkoutTableNameToPreferences(workoutTableName)

            setWorkoutTableName(workoutNameAdd.text.toString().replace(" ", "_"))
            changeFragment(WorkoutFragment::class.java)

            changeNavHeaderText(sharedPreferencesUtils.getWorkoutTableNameFromPreferences())
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

    val updateTextView: String = getString(R.string.edit_indicator) + " " + workoutName
    editIndicator.text = updateTextView

    val updateEditTextHint: String = getString(R.string.rename) + " \"" + workoutName + "\""
    renameWorkout.hint = updateEditTextHint

    dialogBuilder.setView(view)
    dialog = dialogBuilder.create()
    dialog.show()

    updateWorkoutButton.setOnClickListener {
        if (isTextEmpty(renameWorkout).isNotEmpty() && isNotDuplicate(renameWorkout, workoutDB).isNotEmpty()
            && isNotTextNumberOrSymbol(renameWorkout).isNotEmpty()) {

            workoutDB.renameWorkoutTable(workoutName, renameWorkout.text.toString().replace(" ", "_"))

            setWorkoutTableName(renameWorkout.text.toString().replace(" ", "_"))
            changeFragment(WorkoutFragment::class.java)

            changeNavHeaderText(sharedPreferencesUtils.getWorkoutTableNameFromPreferences())
            prepareMenuData()
            expandableAdapter.notifyDataSetChanged()

            dialog.dismiss()
        }
    }

    deleteWorkoutButton.setOnClickListener {
        workoutDB.deleteWorkoutTable(workoutName.replace(" ", "_"))

        setWorkoutTableName(workoutDB.loadLastWorkoutTable())
        changeFragment(WorkoutFragment::class.java)

        changeNavHeaderText(sharedPreferencesUtils.getWorkoutTableNameFromPreferences())
        prepareMenuData()
        expandableAdapter.notifyDataSetChanged()

        dialog.dismiss()
    }
}


fun MainActivity.setWorkoutTableName(workoutName: String) {
    workoutTableName = workoutName
    sharedPreferencesUtils.saveWorkoutTableNameToPreferences(workoutTableName)
}