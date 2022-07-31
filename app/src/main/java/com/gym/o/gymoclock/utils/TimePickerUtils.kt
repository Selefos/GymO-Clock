package com.gym.o.gymoclock.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.gym.o.gymoclock.R

class TimePickerUtils(context: Context) {

    var context: Context? = null

    init {
        this.context = context
    }

    companion object {
        var timePicked = false

        fun isTimePicked(isTimePicked: Boolean) {
            this.timePicked = isTimePicked
        }
    }


    private val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
    lateinit var dialog: AlertDialog
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val viewAddOrEditExercise: View = inflater.inflate(R.layout.exercise_number_pickers, null)

    private val numberPickerMinutes: NumberPicker = viewAddOrEditExercise.findViewById(R.id.numberPicker_minutes)
    private val numberPickerSeconds: NumberPicker = viewAddOrEditExercise.findViewById(R.id.numberPicker_seconds)
    val timeDigitalFormatTextView: TextView = viewAddOrEditExercise.findViewById(R.id.time_digital_format)
    val setNumberPicker: Button = viewAddOrEditExercise.findViewById(R.id.set_numberPicker)
    val cancelTimePicker: Button = viewAddOrEditExercise.findViewById(R.id.cancel_numberPicker)

    fun numberPickerTimeDialogExercises(digitalTime: TextView?, verifyButton: ImageButton) {

        numberPickerMinutes.maxValue = 59
        numberPickerMinutes.value = 0
        numberPickerMinutes.wrapSelectorWheel = true
        numberPickerSeconds.maxValue = 59
        numberPickerSeconds.value = 0

        timeDigitalFormatTextView.text = FormatUtils.convertTimeToDigitalClockMinutes(numberPickerMinutes.value.toString())
        timeDigitalFormatTextView.text = FormatUtils.convertTimeToDigitalClockSeconds(numberPickerSeconds.value.toString())

        numberPickerMinutes.setOnValueChangedListener { picker, oldVal, newVal ->
            timeDigitalFormatTextView.text = FormatUtils.convertTimeToDigitalClockMinutes(newVal.toString())
        }

        numberPickerSeconds.setOnValueChangedListener { picker, oldVal, newVal ->
            timeDigitalFormatTextView.text = FormatUtils.convertTimeToDigitalClockSeconds(newVal.toString())
        }

        dialogBuilder.setView(viewAddOrEditExercise)
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()

        setNumberPicker.setOnClickListener {
            digitalTime?.text = timeDigitalFormatTextView.text
            verifyButton.background.setTintList(ContextCompat.getColorStateList(context!!, R.color.custom_text_color))

            isTimePicked(true)
            dialog.dismiss()
        }

        cancelTimePicker.setOnClickListener {
            Log.i("DismissTime", "Clicked")
            isTimePicked(false)
            dialog.dismiss()
        }
    }

    fun settingsTimeDialogPrepareClock() {

        numberPickerMinutes.maxValue = 59
        numberPickerMinutes.value = 0
        numberPickerMinutes.wrapSelectorWheel = true
        numberPickerSeconds.maxValue = 59
        numberPickerSeconds.value = 0

        timeDigitalFormatTextView.text = FormatUtils.convertTimeToDigitalClockMinutes(numberPickerMinutes.value.toString())
        timeDigitalFormatTextView.text = FormatUtils.convertTimeToDigitalClockSeconds(numberPickerSeconds.value.toString())

        numberPickerMinutes.setOnValueChangedListener { picker, oldVal, newVal ->
            timeDigitalFormatTextView.text = FormatUtils.convertTimeToDigitalClockMinutes(newVal.toString())
        }

        numberPickerSeconds.setOnValueChangedListener { picker, oldVal, newVal ->
            timeDigitalFormatTextView.text = FormatUtils.convertTimeToDigitalClockSeconds(newVal.toString())
        }

        dialogBuilder.setView(viewAddOrEditExercise)
        dialog = dialogBuilder.create()
        dialog.show()

    }

}