package com.gym.o.gymoclock.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import com.gym.o.gymoclock.databases.CalendarDB
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import java.lang.reflect.Field
import java.util.regex.Matcher
import java.util.regex.Pattern

class ErrorUtils {
    companion object {

        fun isNameDuplicate(name: String, context: Context): Boolean {
            val workoutDB = WorkoutDB(context.applicationContext)
            val nameTemp: List<String> = workoutDB.checkForDuplicateNames(workoutTableName)

            for (temp in nameTemp)
                if (name == temp)
                    return true

            return false
        }


        fun editTextWarning(editText: EditText, warning: String) {
            editText.clearFocus()
            if (editText.text.isNotBlank())
                editText.setText("")
            editText.setHintTextColor(Color.RED)
            editText.hint = warning
        }


        fun pickerTextWarning(numberPicker: NumberPicker, warning: Int) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                numberPicker.textColor = Color.RED
            } else {
                val count = numberPicker.childCount
                for (i in 0 until count) {
                    val child = numberPicker.getChildAt(i)
                    if (child is EditText) {
                        try {
                            child.setTextColor(Color.RED)
                            numberPicker.invalidate()
                            val fieldSelectorWheelPaint: Field =
                                numberPicker.javaClass.getDeclaredField("Paint")
                            var isAccessible: Boolean = fieldSelectorWheelPaint.isAccessible
                            fieldSelectorWheelPaint.isAccessible = true
                            val paint: Paint = fieldSelectorWheelPaint.get(numberPicker) as Paint
                            if (paint != null) {
                                paint.color = Color.RED
                                fieldSelectorWheelPaint.isAccessible = isAccessible
                                numberPicker.invalidate()
                            }
                            val fieldSelectionDivider: Field =
                                numberPicker.javaClass.getDeclaredField("Paint")
                            isAccessible = fieldSelectionDivider.isAccessible
                            fieldSelectionDivider.isAccessible = true
                            fieldSelectionDivider.set(numberPicker, null)
                            fieldSelectionDivider.isAccessible = isAccessible
                            numberPicker.invalidate()
                        } catch (ex: Exception) {
                            // Ignore
                        }
                    }
                }
            }

            numberPicker.value = warning
        }


        fun textViewWarning(textView: TextView) {
            textView.setTextColor(Color.RED)
        }


        fun isNotTextNumberOrSymbol(editValue: EditText): String {
            var value: String = editValue.text.toString().trim()
            val findSymbol: Pattern =
                Pattern.compile("[1234567890`~!@#$%^&*()+=;΄¨':\"\\\\/.,|<>?{}\\[\\]-]")
            val inspectChar: Matcher = findSymbol.matcher(value)
            val checkForSymbol: Boolean = inspectChar.find()

            return if (checkForSymbol) {
                editTextWarning(editValue, "No symbols or numbers")
                ""
            } else {
                value = value.replace(" ", "_")
                value
            }
        }


        fun isTextEmpty(editValue: EditText): String {
            val value: String = editValue.text.toString().trim()
            return value.ifEmpty {
                editTextWarning(editValue, "Empty")
                ""
            }
        }


        fun isNotDuplicate(editValue: EditText, workoutDB: WorkoutDB): String {
            val value: String = editValue.text.toString().trim()
            val checkForDuplicate = workoutDB.loadWorkoutTableNames()
            for (name in checkForDuplicate) {
                if (value == name) {
                    editTextWarning(editValue, "Workout Exists")
                    return ""
                }
            }
            return value
        }


        fun tableNameExists(tableName: String, context: Context): Boolean {
            val calendarDB = CalendarDB(context)
            val isExisting = calendarDB.loadCalendarTableNames()
            for (name in isExisting) {
                if (tableName == name.uppercase()) {
                    return true
                }
            }
            return false
        }

    }
}