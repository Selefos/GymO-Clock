package com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.GravityCompat
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.enums.ClockSelected
import com.gym.o.gymoclock.enums.ClockSelectedEnum
import com.gym.o.gymoclock.functionality.workout_pr.recyclerPosition
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseRecyclerAdapter
import com.gym.o.gymoclock.functionality.workout_pr.rounds
import com.gym.o.gymoclock.utils.DateTimeUtils
import com.gym.o.gymoclock.utils.FormatUtils
import com.gym.o.gymoclock.utils.TextToSpeechUtils
import java.util.*

var workTimeInMillis: Long = 0//by Delegates.notNull<Long>()
var restTimeInMillis: Long = 0//by Delegates.notNull<Long>()
var startTime: String = ""
var endTime: Long = 0 // by Delegates.notNull<Long>()

var isWorkAnim = true
var isRestAnim = false

fun ExerciseRecyclerAdapter.startExerciseTimer(positionData: Int) {
    val position = dataList[positionData]

    workTimeInMillis = FormatUtils.convertTimeToMillis(position.exerciseClockValue.text.toString())
    endTime = System.currentTimeMillis() + workTimeInMillis
    if (startTime.isEmpty())
        startTime = DateTimeUtils.getCurrentTime()

    Log.i("Start", "-----START TIME HERE $startTime")
    Log.w("CountDown", "Start Of Timer {$recyclerPosition} -- ${DateTimeUtils.getCurrentTime()}")

    ClockSelected.clockSelected = ClockSelectedEnum.WorkClock
    Log.i("ClockSelected", "$ClockSelected.clockSelected")
    //mRecyclerViewInterface.startClockProgressBar(ClockSelected.clockSelected, workTimeInMillis)

    position.wCountDownTimer = object : CountDownTimer(workTimeInMillis, 1000) {
        override fun onTick(millsUntilFinish: Long) {
            workTimeInMillis = millsUntilFinish

            if(isWorkAnim)
                mRecyclerViewInterface.startClockProgressBar(ClockSelected.clockSelected, workTimeInMillis)
            isWorkAnim = false

            updateExerciseCountUI(positionData)
        }

        override fun onFinish() {
            Log.w("CountDown", "End Of Start Timer {$recyclerPosition} -- ${DateTimeUtils.getCurrentTime()}")
            position.wTimerIsRunning = false
            position.wTimerIsPaused = true
            isRestAnim = true
            startRestTimer(positionData)
        }
    }.start()

    position.wTimerIsRunning = true
}

fun ExerciseRecyclerAdapter.updateExerciseCountUI(positionData: Int) {
    val position = dataList[positionData]
    val minutesWork = (workTimeInMillis / 1000) / 60
    val secondsWork = (workTimeInMillis / 1000) % 60
    val workCount: String = String.format(Locale.getDefault(), "%02d:%02d", minutesWork, secondsWork)

    if (workTimeInMillis / 1000 <= 5 && workTimeInMillis / 1000 != 0L)
        TextToSpeechUtils.getInstance(context).speak((workTimeInMillis / 1000).toString())
    if (workTimeInMillis / 1000 == 0L && recyclerPosition != itemCount - 1)
        TextToSpeechUtils.getInstance(context).speak(resources.getString(R.string.workout_rest))
    else if (workTimeInMillis / 1000 == 0L && recyclerPosition == itemCount - 1 && rounds > 1)
        TextToSpeechUtils.getInstance(context).speak(resources.getString(R.string.rest_interval))
    else if (workTimeInMillis / 1000 == 0L && recyclerPosition == itemCount - 1 && rounds == 1)
        TextToSpeechUtils.getInstance(context).speak(resources.getString(R.string.workout_completed))

    position.exerciseClockValue.text = workCount
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

    restTimeInMillis = FormatUtils.convertTimeToMillis(position.restClockValue.text.toString())
    endTime = System.currentTimeMillis() + restTimeInMillis

    Log.i("CountDown", "Start Of Rest Timer {$recyclerPosition} -- ${DateTimeUtils.getCurrentTime()}")

    ClockSelected.clockSelected = ClockSelectedEnum.RestClock
    //mRecyclerViewInterface.startClockProgressBar(ClockSelected.clockSelected, restTimeInMillis)

    position.rCountDownTimer = object : CountDownTimer(restTimeInMillis, 1000) {

        override fun onTick(millsUntilFinish: Long) {
            restTimeInMillis = millsUntilFinish
            if(isRestAnim)
                mRecyclerViewInterface.startClockProgressBar(ClockSelected.clockSelected, restTimeInMillis)
            isRestAnim = false
            updateRestCountUI(positionData)
        }

        override fun onFinish() {
            position.rTimerIsRunning = false
            position.rTimerIsPaused = true
            isWorkAnim = true
            Log.e("CountDown", "Rest Timer onFinish() {$recyclerPosition} -- ${DateTimeUtils.getCurrentTime()}")

            if (recyclerPosition == itemCount - 1 && totalTimeFromDB(rounds) > 0) {

//                dataList.clear()
//                notifyDataSetChanged()

                // temp fix, recycler view positions load at ui thread
                // if the handler gets removed the 1st positions' 1st clock
                // will not animate after the re-load  and the 2nd or third positions'
                // 2nd clock will have the progress bar not reset by
                /** @see com.gym.o.gymoclock.ui.workout.WorkoutFragment.resetProgressBar **/

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

            Log.d("CountDown", "iterator Scroll Position == $recyclerPosition -- ${DateTimeUtils.getCurrentTime()}")
            Log.i("CountDown", "End Of Rest Timer $recyclerPosition -- ${DateTimeUtils.getCurrentTime()}")
        }
    }.start()
    position.rTimerIsRunning = true
}

fun ExerciseRecyclerAdapter.updateRestCountUI(positionData: Int) {
    val position = dataList[positionData]
    val minutesWork = (restTimeInMillis / 1000) / 60
    val secondsWork = (restTimeInMillis / 1000) % 60
    val restCount: String = String.format(Locale.getDefault(), "%02d:%02d", minutesWork, secondsWork)

    if (restTimeInMillis / 1000 <= 5 && restTimeInMillis / 1000 != 0L)
        TextToSpeechUtils.getInstance(context).speak((restTimeInMillis / 1000).toString())

    if (restTimeInMillis / 1000 == 0L)
        TextToSpeechUtils.getInstance(context).speak(resources.getString(R.string.workout_start))

    position.restClockValue.text = restCount
    notifyItemChanged(positionData, position.restClockValue)
}

fun ExerciseRecyclerAdapter.pauseRestTimer(positionData: Int, speakText: String) {
    val position = dataList[positionData]
    TextToSpeechUtils.getInstance(context).speak(speakText)

    position.rTimerIsRunning = false
    position.rTimerIsPaused = true
    position.rCountDownTimer.cancel()
}
