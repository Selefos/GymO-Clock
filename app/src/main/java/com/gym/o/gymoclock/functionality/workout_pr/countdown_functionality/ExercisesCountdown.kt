package com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.enums.ClockSelected
import com.gym.o.gymoclock.enums.ClockSelectedEnum
import com.gym.o.gymoclock.functionality.workout_pr.animations.exerciseClockAnimate
import com.gym.o.gymoclock.functionality.workout_pr.animations.restClockAnimate
import com.gym.o.gymoclock.functionality.workout_pr.recyclerPosition
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseRecyclerAdapter
import com.gym.o.gymoclock.functionality.workout_pr.rounds
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.TextToSpeechUtils

var workTimeInMillis: Long = 0
var restTimeInMillis: Long = 0
var startTime: String = ""
var endTime: Long = 0

var isExerciseAnimating = true
var isRestAnimating = true

fun ExerciseRecyclerAdapter.startExerciseTimer(positionData: Int) {
    val position = dataList[positionData]

    workTimeInMillis = FormatUtils.convertDigitalTimeToMillis(position.exerciseClockValue.text.toString())
    endTime = System.currentTimeMillis() + workTimeInMillis
    if (startTime.isEmpty())
        startTime = DateTimeUtils.getCurrentTime()

    ClockSelected.clockSelected = ClockSelectedEnum.WorkClock

    position.wCountDownTimer = object : CountDownTimer(workTimeInMillis, 1000) {
        override fun onTick(millsUntilFinish: Long) {
            workTimeInMillis = millsUntilFinish
            updateExerciseCountUI(positionData)
        }

        override fun onFinish() {
            Log.w("CountDown", "End Of Start Timer {$recyclerPosition} -- ${DateTimeUtils.getCurrentTime()}")
            position.wTimerIsRunning = false
            position.wTimerIsPaused = true
            isRestAnimating = true
            startRestTimer(positionData)
        }
    }.start()

    position.wTimerIsRunning = true
}

fun ExerciseRecyclerAdapter.updateExerciseCountUI(positionData: Int) {
    val position = dataList[positionData]

    if (workTimeInMillis / 1000 <= 5 && workTimeInMillis / 1000 != 0L)
        TextToSpeechUtils.getInstance(context).speak((workTimeInMillis / 1000).toString())
    if (workTimeInMillis / 1000 == 0L && recyclerPosition != itemCount - 1)
        TextToSpeechUtils.getInstance(context).speak(resources.getString(R.string.workout_rest))
    else if (workTimeInMillis / 1000 == 0L && recyclerPosition == itemCount - 1 && rounds > 1)
        TextToSpeechUtils.getInstance(context).speak(resources.getString(R.string.rest_interval))
    else if (workTimeInMillis / 1000 == 0L && recyclerPosition == itemCount - 1 && rounds == 1)
        TextToSpeechUtils.getInstance(context).speak(resources.getString(R.string.workout_completed))

    exerciseClockAnimate()
    position.exerciseClockValue.text = FormatUtils.timeInMillisecondsClockUI(workTimeInMillis)//workCount
    notifyItemChanged(positionData, position.exerciseClockValue)
}

fun ExerciseRecyclerAdapter.pauseExerciseTimer(positionData: Int, speakText: String) {
    val position = dataList[positionData]

    TextToSpeechUtils.getInstance(context).speak(speakText)

    position.wTimerIsRunning = false
    position.wTimerIsPaused = true
    position.wCountDownTimer.cancel()
}


fun ExerciseRecyclerAdapter.startRestTimer(positionData: Int) {
    val position = dataList[positionData]

    restTimeInMillis = FormatUtils.convertDigitalTimeToMillis(position.restClockValue.text.toString())
    endTime = System.currentTimeMillis() + restTimeInMillis

    ClockSelected.clockSelected = ClockSelectedEnum.RestClock

    position.rCountDownTimer = object : CountDownTimer(restTimeInMillis, 1000) {

        override fun onTick(millsUntilFinish: Long) {
            restTimeInMillis = millsUntilFinish
            updateRestCountUI(positionData)
        }

        override fun onFinish() {
            position.rTimerIsRunning = false
            position.rTimerIsPaused = true
            isExerciseAnimating = true

            if (recyclerPosition == itemCount - 1 && totalTimeFromDB(rounds) > 0) {

//                dataList.clear()
//                notifyDataSetChanged()


                /**
                 * temp fix, recycler view positions load at ui thread
                 * if the handler gets removed the 1st positions' 1st clock
                 * will not animate after the re-load  and the 2nd or third positions'
                 * 2nd clock will have the progress bar not reset by
                 * @see com.gym.o.gymoclock.ui.workout.WorkoutFragment.resetProgressBar
                 */

                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        mRecyclerViewInterface.loadRecyclerViews()
                        mRecyclerViewInterface.roundsCount()
                    }, 100)
//                mRecyclerViewInterface.loadRecyclerViews()
//                mRecyclerViewInterface.roundsCount()

                Log.d("CountDown", "Rounds Decrease ${totalTimeFromDB(rounds)} -- ${DateTimeUtils.getCurrentTime()}")
                return
            }

            if (recyclerPosition < itemCount - 1 && totalTimeFromDB(rounds) > 0) {
                recyclerPosition++
                Log.d("ITERATOR", "ITERATOR $recyclerPosition -- ${DateTimeUtils.getCurrentTime()}")
                startExerciseTimer(recyclerPosition)
            }

            mRecyclerViewInterface.scrollToPosition()

        }
    }.start()
    position.rTimerIsRunning = true
}

fun ExerciseRecyclerAdapter.updateRestCountUI(positionData: Int) {
    val position = dataList[positionData]

    if (restTimeInMillis / 1000 <= 5 && restTimeInMillis / 1000 != 0L)
        TextToSpeechUtils.getInstance(context).speak((restTimeInMillis / 1000).toString())

    if (restTimeInMillis / 1000 == 0L)
        TextToSpeechUtils.getInstance(context).speak(resources.getString(R.string.workout_start))

    restClockAnimate()
    position.restClockValue.text = FormatUtils.timeInMillisecondsClockUI(restTimeInMillis)//restCount
    notifyItemChanged(positionData, position.restClockValue)
}

fun ExerciseRecyclerAdapter.pauseRestTimer(positionData: Int, speakText: String) {
    val position = dataList[positionData]
    TextToSpeechUtils.getInstance(context).speak(speakText)

    position.rTimerIsRunning = false
    position.rTimerIsPaused = true
    position.rCountDownTimer.cancel()
}