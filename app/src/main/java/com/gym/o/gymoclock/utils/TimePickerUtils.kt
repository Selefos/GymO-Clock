package com.gym.o.gymoclock.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.gym.o.gymoclock.R

class TimePickerUtils(context: Context) {

    var context: Context? = null

    init {
        this.context = context
    }

    fun numberPickerTimeDialog(digitalTime: TextView?) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context!!, R.style.CustomAlertDialog)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val viewAddOrEditExercise: View = inflater.inflate(R.layout.exercise_number_pickers, null)

        val numberPickerMinutes: NumberPicker = viewAddOrEditExercise.findViewById(R.id.numberPicker_minutes)
        val numberPickerSeconds: NumberPicker = viewAddOrEditExercise.findViewById(R.id.numberPicker_seconds)
        val timeDigitalFormatTextView: TextView = viewAddOrEditExercise.findViewById(R.id.time_digital_format)
        val setNumberPicker: Button = viewAddOrEditExercise.findViewById(R.id.set_numberPicker)
        val cancelTimePicker: Button = viewAddOrEditExercise.findViewById(R.id.cancel_numberPicker)

        numberPickerMinutes.maxValue = 59
        numberPickerMinutes.value = 0
        numberPickerMinutes.wrapSelectorWheel = true
        numberPickerSeconds.maxValue = 59
        numberPickerSeconds.value = 0

        timeDigitalFormatTextView.text = ConvertDigitalClocksUtils.convertTimeToDigitalClockMinutes(numberPickerMinutes.value.toString())
        timeDigitalFormatTextView.text = ConvertDigitalClocksUtils.convertTimeToDigitalClockSeconds(numberPickerSeconds.value.toString())

        numberPickerMinutes.setOnValueChangedListener { picker, oldVal, newVal ->
            timeDigitalFormatTextView.text = ConvertDigitalClocksUtils.convertTimeToDigitalClockMinutes(newVal.toString())
        }

        numberPickerSeconds.setOnValueChangedListener { picker, oldVal, newVal ->
            timeDigitalFormatTextView.text = ConvertDigitalClocksUtils.convertTimeToDigitalClockSeconds(newVal.toString())
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