package com.gym.o.gymoclock.functionality.main_activity_pr

import android.widget.EditText
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.utils.WidgetsWarningsUtils
import java.util.regex.Matcher
import java.util.regex.Pattern

fun MainActivity.isNotTextNumberOrSymbol(editValue: EditText): String {
    var value: String = editValue.text.toString().trim()
    val findSymbol: Pattern =
        Pattern.compile("[1234567890`~!@#$%^&*()+=;΄¨':\"\\\\/.,|<>?{}\\[\\]-]")
    val inspectChar: Matcher = findSymbol.matcher(value)
    val checkForSymbol: Boolean = inspectChar.find()

    return if (checkForSymbol) {
        WidgetsWarningsUtils.editTextWarning(editValue, "No symbols or numbers")
        ""
    } else {
        value = value.replace(" ", "_")
        value
    }
}


fun MainActivity.isTextEmpty(editValue: EditText): String {
    val value: String = editValue.text.toString().trim()
    return value.ifEmpty {
        WidgetsWarningsUtils.editTextWarning(editValue, "Empty Name")
        ""
    }
}


fun MainActivity.isNotDuplicate(editValue: EditText, workoutDB: WorkoutDB): String {
    val value: String = editValue.text.toString().trim()
    val checkForDuplicate = workoutDB.loadWorkoutTableNames()
    for (name in checkForDuplicate) {
        if (value == name) {
            WidgetsWarningsUtils.editTextWarning(editValue, "Workout Exists")
            return ""
        }
    }
    return value
}