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

    private val viewAddOrEditExercise: View = inflater.inflate(R.layout.dialog_edit_workout, null)
    private val inflaterRecycler: LayoutInflater = LayoutInflater.from(context)
    val viewRecycler: View = inflaterRecycler.inflate(R.layout.add_view, null)

    val exerciseNameEdit: EditText = viewAddOrEditExercise.findViewById(R.id.exercise_name_edit)
    var workDigitalTime: TextView = viewAddOrEditExercise.findViewById(R.id.work_digital_time)
    val restDigitalTime: TextView = viewAddOrEditExercise.findViewById(R.id.rest_digital_time)

    private val workTimePicker: Button = viewAddOrEditExercise.findViewById(R.id.work_time_picker)
    private val restTimePicker: Button = viewAddOrEditExercise.findViewById(R.id.rest_time_picker)

    private val okButtonAddOrEdit: ImageButton = viewAddOrEditExercise.findViewById(R.id.ok_button)
    private val cancelButtonAddOrEdit: ImageButton = viewAddOrEditExercise.findViewById(R.id.cancel_button)

    private val viewRemoveExercise: View = inflater.inflate(R.layout.delete_exercise, null)

    val okButtonRemoveExercise: Button = viewRemoveExercise.findViewById(R.id.verify_exercise_delete)

    val cancelButtonRemoveExercise: Button = viewRemoveExercise.findViewById(R.id.cancel_exercise_delete)


    fun addOrEditExercise(setCanceledOnTouchOutside: Boolean) {
        dialogBuilder.setView(viewAddOrEditExercise)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()
    }

    fun removeExercise(setCanceledOnTouchOutside: Boolean) {
        dialogBuilder.setView(viewRemoveExercise)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
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




    fun addWorkout(setCanceledOnTouchOutside: Boolean){}

    fun removeRenameWorkout(setCanceledOnTouchOutside: Boolean){}



    private val viewCalendarWorkoutDetails: View = inflater.inflate(R.layout.dialog_calendar_workout_details, null)
    val calendarDate: TextView = viewCalendarWorkoutDetails.findViewById(R.id.calendar_date)
    val calendarStartTime: TextView = viewCalendarWorkoutDetails.findViewById(R.id.calendar_start_time)
    val calendarEndTime: TextView = viewCalendarWorkoutDetails.findViewById(R.id.calendar_end_time)
    val calendarWorkoutName: TextView = viewCalendarWorkoutDetails.findViewById(R.id.calendar_workout_name)
    val calendarTotalTime: TextView = viewCalendarWorkoutDetails.findViewById(R.id.calendar_total_time)
    val calendarTotalWorkingTime: TextView = viewCalendarWorkoutDetails.findViewById(R.id.calendar_total_working_time)
    val calendarCancelButton: ImageButton = viewCalendarWorkoutDetails.findViewById(R.id.calendar_cancel_button)
    fun calendarWorkoutDetails(setCanceledOnTouchOutside: Boolean){
        dialogBuilder.setView(viewCalendarWorkoutDetails)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside)
        dialog.show()

        calendarCancelButton.setOnClickListener{
            dialog.dismiss()
        }
    }

}