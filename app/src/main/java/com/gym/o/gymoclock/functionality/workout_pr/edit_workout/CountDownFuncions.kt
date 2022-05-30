package com.gym.o.gymoclock.functionality.workout_pr.edit_workout

import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import com.gym.o.gymoclock.functionality.calendar_pr.database.CalendarDB
import com.gym.o.gymoclock.functionality.calendar_pr.insertionFunctions.DateTimeUtils
import com.gym.o.gymoclock.functionality.workout_pr.user_adapter.UserAddActivityAdapter
import java.util.*
import kotlin.properties.Delegates

var workTimeInMillis by Delegates.notNull<Long>()
var restTimeInMillis by Delegates.notNull<Long>()
var startTime: String = ""

//region Exercise Timer
@RequiresApi(Build.VERSION_CODES.O)
fun UserAddActivityAdapter.startExerciseTimer(positionData: Int) {
    val position = dataList[positionData]
    workTimeInMillis = convertTimeToMillis(position.exerciseClockValue.text.toString())

    if (startTime.isBlank()) {
        dateTimeUtils = DateTimeUtils()
        startTime = dateTimeUtils.getCurrentTime()
    }
    Log.w("CountDown", "Start Of Timer {$iterator} -- ${dateTimeUtils.getCurrentTime()}")
    position.wCountDownTimer = object : CountDownTimer(workTimeInMillis, 1000) {
        override fun onTick(millsUntilFinish: Long) {
            workTimeInMillis = millsUntilFinish

            updateWorkCountUI(positionData)
        }

        override fun onFinish() {
            dateTimeUtils = DateTimeUtils()
            Log.w(
                "CountDown",
                "End Of Start Timer {$iterator} -- ${dateTimeUtils.getCurrentTime()}"
            )
            position.wTimerIsRunning = false
            position.wTimerIsPaused = true
            startRestTimer(positionData)
        }
    }.start()

    position.wTimerIsRunning = true
}

fun UserAddActivityAdapter.pauseExerciseTimer(positionData: Int) {
    val position = dataList[positionData]

    speak("EXERCISE PAUSED")

    position.wTimerIsRunning = false
    position.wTimerIsPaused = true
    position.wCountDownTimer.cancel()
}

fun UserAddActivityAdapter.updateWorkCountUI(positionData: Int) {
    val position = dataList[positionData]
    val minutesWork = (workTimeInMillis / 1000) / 60
    val secondsWork = (workTimeInMillis / 1000) % 60
    val workCount: String =
        String.format(Locale.getDefault(), "%02d:%02d", minutesWork, secondsWork)

    if (workTimeInMillis / 1000 <= 5 && workTimeInMillis / 1000 != 0L)
        speak((workTimeInMillis / 1000).toString())
    if (workTimeInMillis / 1000 == 0L && iterator != itemCount - 1)
        speak("REST")
    else if (workTimeInMillis / 1000 == 0L && iterator == itemCount - 1 && rounds > 1)
        speak("REST INTERVAL")
    else if (workTimeInMillis / 1000 == 0L && iterator == itemCount - 1 && rounds == 1)
        speak("WORKOUT COMPLETED")

    position.exerciseClockValue.text = workCount
    notifyItemChanged(positionData, position.exerciseClockValue)
}
//endregion

//region Rest Timer
@RequiresApi(Build.VERSION_CODES.O)
fun UserAddActivityAdapter.startRestTimer(positionData: Int) {
    val position = dataList[positionData]
    restTimeInMillis = convertTimeToMillis(position.restClockValue.text.toString())

    Log.i("CountDown", "Start Of Rest Timer {$iterator} -- ${dateTimeUtils.getCurrentTime()}")
    position.rCountDownTimer = object : CountDownTimer(restTimeInMillis, 1000) {

        override fun onTick(millsUntilFinish: Long) {
            restTimeInMillis = millsUntilFinish

            updateRestCountUI(positionData)
        }

        override fun onFinish() {
            position.rTimerIsRunning = false
            position.rTimerIsPaused = true
            dateTimeUtils = DateTimeUtils()
            Log.e("CountDown", "Rest Timer onFinish() {$iterator} -- ${dateTimeUtils.getCurrentTime()}")

            if (iterator == itemCount - 1 && rounds > 0) {

                dataList.clear()
                notifyDataSetChanged()
                mRecyclerViewInterface.loadRecyclerViews()
                mRecyclerViewInterface.roundsCount()

                Log.d("CountDown", "Rounds Decrease $rounds -- ${dateTimeUtils.getCurrentTime()}")
                return
            }
            if (iterator < itemCount - 1 && rounds > 0) {

                iterator++
                Log.d("ITERATOR", "ITERATOR {$iterator} -- ${dateTimeUtils.getCurrentTime()}")
                startExerciseTimer(iterator)
            }
            if (rounds == 0) {

                calendarDB = CalendarDB(context)
                dateTimeUtils = DateTimeUtils()
                Log.d("CountDown", "rounds == 0 ${dateTimeUtils.getCurrentTime()}")

                val monthYear = "${dateTimeUtils.getCurrentMonth()} ${dateTimeUtils.getCurrentYear()}".replace(" ", "_")
                calendarDB.insertCalendarDetails(monthYear, dateTimeUtils.getDate(), startTime, dateTimeUtils.getCurrentTime(), workoutName,
                    convertTimeToDigitalClock(totalTime(5).toString()), convertTimeToDigitalClock(totalWorkingTime(5).toString())
                )
                startTime = ""
            }
            mRecyclerViewInterface.scrollToPosition()

            Log.d("CountDown", "iterator Scroll Position == $iterator -- ${dateTimeUtils.getCurrentTime()}")
            Log.i("CountDown", "End Of Rest Timer $iterator -- ${dateTimeUtils.getCurrentTime()}")

        }
    }.start()
    position.rTimerIsRunning = true
}

fun UserAddActivityAdapter.pauseRestTimer(positionData: Int) {
    val position = dataList[positionData]
    speak("REST PAUSED")

    position.rTimerIsRunning = false
    position.rTimerIsPaused = true
    position.rCountDownTimer.cancel()
}

fun UserAddActivityAdapter.updateRestCountUI(positionData: Int) {
    val position = dataList[positionData]
    val minutesWork = (restTimeInMillis / 1000) / 60
    val secondsWork = (restTimeInMillis / 1000) % 60
    val restCount: String =
        String.format(Locale.getDefault(), "%02d:%02d", minutesWork, secondsWork)

    if (restTimeInMillis / 1000 <= 5 && restTimeInMillis / 1000 != 0L)
        speak((restTimeInMillis / 1000).toString())
    if (restTimeInMillis / 1000 == 0L)
        speak("START")

    position.restClockValue.text = restCount
    notifyItemChanged(positionData, position.restClockValue)
}
//endregion