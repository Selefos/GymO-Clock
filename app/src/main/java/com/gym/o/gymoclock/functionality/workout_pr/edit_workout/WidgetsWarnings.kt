package com.gym.o.gymoclock.functionality.workout_pr.edit_workout

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.gym.o.gymoclock.R

fun editTextWarning(editText: EditText, warning: String) {
    editText.clearFocus()
    editText.setTextColor(Color.RED)
    editText.setText(warning)
}


@RequiresApi(Build.VERSION_CODES.Q)
fun pickerTextWarning(numberPicker: NumberPicker, warning: Int) {

    numberPicker.textColor = Color.RED
    numberPicker.value = warning
}

fun textViewWarning(textView: TextView) {
    textView.setTextColor(Color.RED)
}