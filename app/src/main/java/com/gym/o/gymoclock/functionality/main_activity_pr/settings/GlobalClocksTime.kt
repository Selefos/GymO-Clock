package com.gym.o.gymoclock.functionality.main_activity_pr.settings

import android.util.Log
import android.widget.ExpandableListView
import com.gym.o.gymoclock.MainActivity
import com.gym.o.gymoclock.enums.ClockSelectedEnum
import com.gym.o.gymoclock.functionality.workout_pr.globalRestTimer
import com.gym.o.gymoclock.functionality.workout_pr.globalWorkTimer
import com.gym.o.gymoclock.functionality.workout_pr.prepareCountdownInMillis
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.SharedPreferencesUtils
import com.gym.o.gymoclock.utils.TimePickerUtils

fun MainActivity.prepareTimer(parent: ExpandableListView, groupPosition: Int){
    val timePickerUtils = TimePickerUtils(this)
    timePickerUtils.settingsTimeDialogPrepareClock()

    timePickerUtils.setNumberPickerButton.setOnClickListener {
        prepareCountdownInMillis = if(FormatUtils.convertDigitalTimeToMillis(timePickerUtils.timeDigitalFormatTextView.text.toString()) == 0L )
            1000L
        else
            FormatUtils.convertDigitalTimeToMillis(timePickerUtils.timeDigitalFormatTextView.text.toString())
        SharedPreferencesUtils.savePrepareTimeToPreferences(this, prepareCountdownInMillis)
        timePickerUtils.dialog.dismiss()
        parent.collapseGroup(groupPosition)
    }

    timePickerUtils.cancelTimePickerButton.setOnClickListener {
        Log.i("DismissTime", "Clicked")
        timePickerUtils.dialog.dismiss()
    }
}

fun MainActivity.workTimer(parent: ExpandableListView, groupPosition: Int, workoutName: String){
    val timePickerUtils = TimePickerUtils(this)

    timePickerUtils.settingsTimeDialogPrepareClock()

    timePickerUtils.setNumberPickerButton.setOnClickListener {
        globalWorkTimer = FormatUtils.convertDigitalTimeToSeconds(timePickerUtils.timeDigitalFormatTextView.text.toString())
        workoutDB.updateAllExercisesTimer(workoutName, ClockSelectedEnum.WorkClock, globalWorkTimer)
        changeFragment(WorkoutFragment::class.java)
        timePickerUtils.dialog.dismiss()
        parent.collapseGroup(groupPosition)
    }

    timePickerUtils.cancelTimePickerButton.setOnClickListener {
        Log.i("DismissTime", "Clicked")
        timePickerUtils.dialog.dismiss()
    }

}

fun MainActivity.restTimer(parent: ExpandableListView, groupPosition: Int, workoutName: String){
    val timePickerUtils = TimePickerUtils(this)

    timePickerUtils.settingsTimeDialogPrepareClock()

    timePickerUtils.setNumberPickerButton.setOnClickListener {
        globalRestTimer = FormatUtils.convertDigitalTimeToSeconds(timePickerUtils.timeDigitalFormatTextView.text.toString())
        workoutDB.updateAllExercisesTimer(workoutName, ClockSelectedEnum.RestClock, globalRestTimer)
        changeFragment(WorkoutFragment::class.java)
        timePickerUtils.dialog.dismiss()
        parent.collapseGroup(groupPosition)
    }

    timePickerUtils.cancelTimePickerButton.setOnClickListener {
        Log.i("DismissTime", "Clicked")
        timePickerUtils.dialog.dismiss()
    }
}