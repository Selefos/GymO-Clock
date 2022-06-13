package com.gym.o.gymoclock.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.ConvertTime

class TimePickerUtils(context: Context)  {

    var context: Context? = null

    init{
        this.context = context
    }

    fun numberPickerTimeDialog(digitalTime: TextView?){
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context!!, R.style.CustomAlertDialog)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val viewAddOrEditExercise: View = inflater.inflate(R.layout.exercise_number_pickers, null)

        val numberPickerMinutes = viewAddOrEditExercise.findViewById<NumberPicker>(R.id.numberPicker_minutes)
        val numberPickerSeconds = viewAddOrEditExercise.findViewById<NumberPicker>(R.id.numberPicker_seconds)
        val timeDigitalFormatTextView = viewAddOrEditExercise.findViewById<TextView>(R.id.time_digital_format)
        val setNumberPicker = viewAddOrEditExercise.findViewById<Button>(R.id.set_numberPicker)
        val cancelTimePicker = viewAddOrEditExercise.findViewById<Button>(R.id.cancel_numberPicker)

        numberPickerMinutes.maxValue = 59
        numberPickerMinutes.value = 0
        numberPickerMinutes.wrapSelectorWheel = true
        numberPickerSeconds.maxValue = 59
        numberPickerSeconds.value = 0

        timeDigitalFormatTextView.text = ConvertTime.convertTimeToDigitalClockMinutes(numberPickerMinutes.value.toString())
        timeDigitalFormatTextView.text = ConvertTime.convertTimeToDigitalClockSeconds(numberPickerSeconds.value.toString())

        numberPickerMinutes.setOnValueChangedListener { picker, oldVal, newVal ->

            timeDigitalFormatTextView.text = ConvertTime.convertTimeToDigitalClockMinutes(newVal.toString())
        }

        numberPickerSeconds.setOnValueChangedListener { picker, oldVal, newVal ->

            timeDigitalFormatTextView.text = ConvertTime.convertTimeToDigitalClockSeconds(newVal.toString())
        }

        dialogBuilder.setView(viewAddOrEditExercise)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()

        setNumberPicker.setOnClickListener {
            digitalTime?.text = timeDigitalFormatTextView.text
            dialog.dismiss()
        }

        cancelTimePicker.setOnClickListener {
            dialog.dismiss()
        }
    }
}