package com.gym.o.gymoclock.utils

import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import java.lang.reflect.Field

class WidgetsWarningsUtils {

    companion object {

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

    }
}