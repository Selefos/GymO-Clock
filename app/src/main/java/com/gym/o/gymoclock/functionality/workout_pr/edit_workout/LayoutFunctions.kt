package com.gym.o.gymoclock.functionality.workout_pr.edit_workout

import android.graphics.Color
import android.widget.EditText
import android.widget.TextView

fun editTextWarning(editText: EditText, warning: String) {
    editText.clearFocus()
    editText.setTextColor(Color.RED)
    editText.setText(warning)
}

fun textViewWarning(textView: TextView) {
    textView.setTextColor(Color.RED)
}