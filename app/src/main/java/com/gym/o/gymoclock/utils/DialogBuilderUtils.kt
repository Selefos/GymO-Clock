package com.gym.o.gymoclock.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.gym.o.gymoclock.R


class DialogBuilderUtils(context: Context) : AlertDialog.Builder(context) {

    private var dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
    lateinit var dialog: AlertDialog
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val viewAddOrEditExercise: View = inflater.inflate(R.layout.edit_workout, null)
    private val inflaterRecycler: LayoutInflater = LayoutInflater.from(context)
    val viewRecycler: View = inflaterRecycler.inflate(R.layout.add_view, null)

    val exerciseNameEdit: EditText = viewAddOrEditExercise.findViewById(R.id.exercise_name_edit)
    val workDigitalTime: TextView = viewAddOrEditExercise.findViewById(R.id.work_digital_time)
    val restDigitalTime: TextView = viewAddOrEditExercise.findViewById(R.id.rest_digital_time)

    val workTimePicker: Button = viewAddOrEditExercise.findViewById(R.id.work_time_picker)
    val restTimePicker: Button = viewAddOrEditExercise.findViewById(R.id.rest_time_picker)

    private val okButtonAddOrEdit: ImageButton = viewAddOrEditExercise.findViewById(R.id.ok_button)
    private val cancelButtonAddOrEdit: ImageButton = viewAddOrEditExercise.findViewById(R.id.cancel_button)

    private val viewRemoveExercise: View = inflater.inflate(R.layout.delete_exercise, null)
    val okButtonRemoveExercise: Button = viewRemoveExercise.findViewById(R.id.verify_exercise_delete)
    val cancelButtonRemoveExercise: Button = viewRemoveExercise.findViewById(R.id.cancel_exercise_delete)

    fun dialogBuilderAddOrEditExercise() {
        dialogBuilder.setView(viewAddOrEditExercise)
        dialog = dialogBuilder.create()
        dialog.show()
    }

    fun dialogBuilderRemoveExercise() {
        dialogBuilder.setView(viewRemoveExercise)
        dialog = dialogBuilder.create()
        dialog.show()

    }

    fun onClickListener(addEditExercise: () -> Unit, numberPickerTimeDialogWork: () -> Unit, numberPickerTimeDialogRest: () -> Unit) {
        okButtonAddOrEdit.setOnClickListener {
            addEditExercise()
        }

        cancelButtonAddOrEdit.setOnClickListener {
            dialog.dismiss()
        }

        workTimePicker.setOnClickListener {
            numberPickerTimeDialogWork()
        }

        restTimePicker.setOnClickListener {
            numberPickerTimeDialogRest()
        }
    }

}