package com.gym.o.gymoclock.functionality.workout_pr.rounds_picker

import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.Log
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functions.pauseTotalTimer
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functions.startTotalTimer
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.ConvertTime
import com.gym.o.gymoclock.functionality.workout_pr.rounds
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import java.lang.reflect.Field
import kotlin.properties.Delegates


private var timeDifference by Delegates.notNull<Int>()

fun WorkoutFragment.roundsPicker() {

    binding.roundsEdit.minValue = 1
    binding.roundsEdit.maxValue = 20
    binding.roundsEdit.value = rounds

    Log.d(TAG_NUMPICKER, "${binding.roundsEdit.value}")

    binding.roundsEdit.setOnScrollListener { view, scrollState ->
        when (scrollState) {
            NumberPicker.OnScrollListener.SCROLL_STATE_IDLE -> onScrollIdle(view)
            NumberPicker.OnScrollListener.SCROLL_STATE_FLING -> onScrollFlying()
            NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> onScrollTouched()
        }
    }

}

fun WorkoutFragment.onScrollIdle(scrollView: NumberPicker) {

    Log.i(TAG_NUMPICKER, "Scroll Idle")

    val oldValue = rounds
    Log.d(TAG_NUMPICKER, "OldValue= $oldValue")

    rounds = scrollView.value
    saveRoundsValueToPreferences(scrollView)
    Log.d(TAG_NUMPICKER, "Value rounds = $rounds")

    timeDifference = timeDifference(listAdapter.totalTime(oldValue))
    binding.totalTime.text =
        ConvertTime.convertTimeToDigitalClock(refreshTotalTimeOnRoundsChanged(timeDifference).toString())

    if (isStartWorkout) {
        pauseTotalTimer()
        startTotalTimer()
    }

    if (scrollView.value == 0) roundPickerColor(
        binding.roundsEdit,
        Color.RED
    )//binding.roundsEdit.textColor = Color.RED
    else roundPickerColor(
        binding.roundsEdit, ContextCompat.getColor(
            requireContext(),
            R.color.number_picker_scroll_idle
        )
    )
//        binding.roundsEdit.textColor = getColor(
//            requireContext(),
//            R.color.number_picker_scroll_idle
//        )

    if (scrollView.value == 1) {
        dataList[listAdapter.itemCount - 1].restClockValue.text =
            ConvertTime.convertTimeToDigitalClock("0")
    }

}

fun WorkoutFragment.onScrollFlying() {
    Log.i("NumberPicker", "Scroll Flying")
    roundPickerColor(
        binding.roundsEdit, ContextCompat.getColor(
            requireContext(),
            R.color.number_picker_scroll_flying
        )
    )
//        binding.roundsEdit.textColor = getColor(
//            requireContext(),
//            R.color.number_picker_scroll_flying
//        )
}

fun WorkoutFragment.onScrollTouched() {
    Log.i(TAG_NUMPICKER, "Scroll Touch Scroll")
    roundPickerColor(
        binding.roundsEdit, ContextCompat.getColor(
            requireContext(),
            R.color.number_picker_scroll_touched
        )
    )
//        binding.roundsEdit.textColor = getColor(
//            requireContext(),
//            R.color.number_picker_scroll_touched
//        )
}

fun WorkoutFragment.saveRoundsValueToPreferences(scrollView: NumberPicker) {
    val save = sharedPreferences.edit()
    save.putInt("roundsInt", scrollView.value)
    save.apply()
    Log.i(TAG_NUMPICKER, "Round Value Saved: New Value = ${scrollView.value}")
}

fun WorkoutFragment.refreshTotalTimeOnRoundsChanged(timeDifference: Int): Int {
    return (listAdapter.totalTime(rounds)) - timeDifference
}

fun WorkoutFragment.timeDifference(totalTime: Int): Int {
    val currentTotalTime: Int =
        ConvertTime.convertTimeToSeconds(binding.totalTime.text.toString()).toInt()
    return totalTime - currentTotalTime
}

fun roundPickerColor(numberPicker: NumberPicker, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        numberPicker.textColor = color

    } else {
        val count = numberPicker.childCount
        for (i in 0 until count) {
            val child = numberPicker.getChildAt(i)
            if (child is EditText) {
                try {
                    child.setTextColor(color)
                    numberPicker.invalidate()
                    val fieldSelectorWheelPaint: Field =
                        numberPicker.javaClass.getDeclaredField("mSelectorWheelPaint")
                    var isAccessible: Boolean = fieldSelectorWheelPaint.isAccessible
                    fieldSelectorWheelPaint.isAccessible = true
                    val paint: Paint = fieldSelectorWheelPaint.get(numberPicker) as Paint
                    if (paint != null) {
                        paint.color = color
                        fieldSelectorWheelPaint.isAccessible = isAccessible
                        numberPicker.invalidate()
                    }
                    val fieldSelectionDivider: Field =
                        numberPicker.javaClass.getDeclaredField("mSelectorWheelPaint")
                    isAccessible = fieldSelectionDivider.isAccessible
                    fieldSelectionDivider.isAccessible = true
                    fieldSelectionDivider.set(numberPicker, null)
                    fieldSelectionDivider.isAccessible = isAccessible
                    numberPicker.invalidate()
                } catch (ex: Exception) {
                    Log.e("NumberPickerColor", "Field Selection Exception")
                }
            }
        }
    }
}
