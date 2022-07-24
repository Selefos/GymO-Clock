package com.gym.o.gymoclock.functionality.workout_pr.rounds_picker

import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.Log
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.functionality.workout_pr.rounds
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils
import java.lang.reflect.Field
import kotlin.properties.Delegates

private var elapsedTime by Delegates.notNull<Int>()


fun WorkoutFragment.roundsPicker() {

    binding.roundsPicker.minValue = 1
    binding.roundsPicker.maxValue = 20
    binding.roundsPicker.value = rounds

    Log.d(TAG_NUMPICKER, "${binding.roundsPicker.value}")

    binding.roundsPicker.setOnScrollListener { view, scrollState ->
        when (scrollState) {
            NumberPicker.OnScrollListener.SCROLL_STATE_IDLE         -> onScrollIdle(view)
            NumberPicker.OnScrollListener.SCROLL_STATE_FLING        -> onScrollFlying()
            NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> onScrollTouched()
        }
    }

    binding.roundsPicker.setOnValueChangedListener { picker, oldVal, newVal ->
        Log.i(TAG_NUMPICKER, "Scroll Idle")

        Log.d(TAG_NUMPICKER, "OldValue= $oldVal")

        rounds = picker.value

        SharedPreferencesUtils.saveRoundsValueToPreferences(requireContext(), picker)
        Log.d(TAG_NUMPICKER, "Value rounds = $rounds")


        //elapsedTime = elapsedTime(listAdapter.totalTimeFromDB(oldVal))
        binding.totalTime.text = FormatUtils.convertTimeToDigitalClock(
            //listAdapter.totalTimeFromView(rounds).toString()
            //refreshTotalTimeOnRoundsChanged(elapsedTime, newVal).toString()
            (listAdapter.totalTimeFromDB(newVal)).toString()
        )

        lastRestTimeCheck()
    }

}


fun WorkoutFragment.elapsedTime(totalTimeFromDB: Int): Int {
    val currentTotalTime: Int = FormatUtils.convertTimeToSeconds(binding.totalTime.text.toString())
    Log.i("ELAPSED_TIME", "TOTAL TIME DB: $totalTimeFromDB, CURRENT TOTAL TIME: $currentTotalTime, TOTAL TIME TXT: ${binding.totalTime.text}")
    return totalTimeFromDB - currentTotalTime
}


fun WorkoutFragment.refreshTotalTimeOnRoundsChanged(elapsedTime: Int, newVal: Int): Int {
    Log.i("REFRESH_TIME", "NEW VAL: $newVal,  ELAPSED TIME: $elapsedTime")
    Log.i("REFRESH_TIME", "TOTAL TIME FROM DB: ${listAdapter.totalTimeFromDB(newVal)}, ELAPSED TIME: $elapsedTime, " +
            "NEW TOTAL COUNTDOWN: ${listAdapter.totalTimeFromDB(newVal) - elapsedTime}")
    return listAdapter.totalTimeFromDB(newVal) - elapsedTime
}


fun WorkoutFragment.onScrollIdle(scrollView: NumberPicker?) {
    if (scrollView!!.value == 0) roundPickerColor(binding.roundsPicker, Color.RED)
    else roundPickerColor(binding.roundsPicker, ContextCompat.getColor(requireContext(), R.color.number_picker_scroll_idle))
}


fun WorkoutFragment.onScrollFlying() {
    Log.i("NumberPickerDialogs", "Scroll Flying")
    roundPickerColor(binding.roundsPicker, ContextCompat.getColor(requireContext(), R.color.number_picker_scroll_flying))
}


fun WorkoutFragment.onScrollTouched() {
    Log.i(TAG_NUMPICKER, "Scroll Touch Scroll")
    roundPickerColor(binding.roundsPicker, ContextCompat.getColor(requireContext(), R.color.number_picker_scroll_touched))
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