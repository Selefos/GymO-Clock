package com.gym.o.gymoclock.functionality.workout_pr.user_adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.RecyclerViewInterface
import com.gym.o.gymoclock.functionality.calendar_pr.database.CalendarDB
import com.gym.o.gymoclock.functionality.calendar_pr.insertionFunctions.DateTimeUtils
import com.gym.o.gymoclock.functionality.workout_pr.database.WorkoutDB
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.*
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import java.util.*
import kotlin.properties.Delegates


class UserAddActivityAdapter(var context: Context, val mRecyclerViewInterface: RecyclerViewInterface, val dataList: ArrayList<Elements>) :
    RecyclerView.Adapter<UserAddActivityAdapter.ViewHolder>() {

    lateinit var mTTS: TextToSpeech
    lateinit var calendarDB: CalendarDB
    lateinit var dateTimeUtils: DateTimeUtils

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var exerciseName: TextView = itemView.findViewById(R.id.exercise_name)
        var exerciseClock: TextView = itemView.findViewById(R.id.countdown_work)
        var restClock: TextView = itemView.findViewById(R.id.countdown_rest)

        var exerciseImg: ImageButton = itemView.findViewById(R.id.exercise_img)
        var restImg: ImageButton = itemView.findViewById(R.id.rest_img)
        val removeView: ImageButton = itemView.findViewById(R.id.remove_view)

        var mwTimerIsRunning by Delegates.notNull<Boolean>()
        var mwTimerIsPaused by Delegates.notNull<Boolean>()
        var mwTimerIsStopped by Delegates.notNull<Boolean>()
        var mrTimerIsRunning by Delegates.notNull<Boolean>()
        var mrTimerIsPaused by Delegates.notNull<Boolean>()
        var mrTimerIsStopped by Delegates.notNull<Boolean>()

        init {

            val playPause: ImageButton = itemView.findViewById(R.id.play_pause)
            playPause.setOnClickListener {
                //roundsDecrease()
            }

            val editView: ImageButton = itemView.findViewById(R.id.edit_view)
            editView.setOnClickListener { mRecyclerViewInterface.editExercise(adapterPosition) }

            mTTS = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = mTTS.setLanguage(Locale.ENGLISH)
                    if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        Log.e("TTS", "Language not supported")
                    }
                } else {
                    Log.e("TTS", "Initialization failed")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.add_view, parent, false)
        return ViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newList = dataList[position]
        holder.exerciseName.text = newList.exerciseNameValue

        holder.exerciseClock.text = newList.exerciseClockValue.text
        holder.mwTimerIsRunning = newList.wTimerIsRunning
        holder.mwTimerIsPaused = newList.wTimerIsPaused

        holder.restClock.text = newList.restClockValue.text
        holder.mrTimerIsRunning = newList.rTimerIsRunning
        holder.mrTimerIsPaused = newList.rTimerIsPaused

        setOnAddViewAnimation(holder.itemView, position)

        holder.removeView.setOnClickListener{ mRecyclerViewInterface.removeExercise(holder.itemView, holder.adapterPosition) }
        holder.exerciseImg.setOnClickListener {
            if (newList.wTimerIsRunning) pauseExerciseTimer(position)
            else {
                if (holder.exerciseClock.text.toString() == "00:00")
                    Toast.makeText(
                        context,
                        "Position: $position uninitialized timer",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    startExerciseTimer(position)
                    Toast.makeText(context, "Position: $position START", Toast.LENGTH_SHORT).show()
                }
            }

        }
        holder.restImg.setOnClickListener {
            if (newList.rTimerIsRunning) pauseRestTimer(position)
            else {
                if (holder.restClock.text.toString() == "00:00")
                    Toast.makeText(
                        context,
                        "Position: $position uninitialized timer",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    if (newList.wTimerIsPaused)
                        startRestTimer(position)
                    else
                        Toast.makeText(
                            context,
                            "Position: $position workout ongoing",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private lateinit var workoutDB: WorkoutDB
    fun totalTime(rounds: Int): Int {
        var totalTime = 0
        workoutDB = WorkoutDB(context)
        val cursor: Cursor = workoutDB.loadRecyclerElements(workoutName)

        if (cursor.moveToFirst()) {
            do {
                totalTime += cursor.getInt(2)
                totalTime += cursor.getInt(3)

            } while (cursor.moveToNext())
        }
        cursor.close()

        return totalTime * rounds
    }

    fun totalWorkingTime(rounds: Int): Int {
        var totalTime = 0
        workoutDB = WorkoutDB(context)
        val cursor: Cursor = workoutDB.loadRecyclerElements(workoutName)

        if (cursor.moveToFirst()) {
            do {
                totalTime += cursor.getInt(2)

            } while (cursor.moveToNext())
        }
        cursor.close()

        return totalTime * rounds
    }

    fun speak(voiceText: String) {
        var pitch = 50.toFloat() / 50
        if (pitch < 0.1) pitch = 0.1f
        var speed = 50.toFloat() / 50
        if (speed < 0.1) speed = 0.1f
        mTTS.setPitch(pitch)
        mTTS.setSpeechRate(speed)
        mTTS.speak(voiceText, TextToSpeech.QUEUE_FLUSH, null)
    }

    private fun setOnAddViewAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > getLastPositionForAddViewAnimation) {
            val anim = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim.duration = 1000
            viewToAnimate.startAnimation(anim)
            getLastPositionForAddViewAnimation = position
        }
    }

    fun setOnRemoveViewAnimation(viewToAnimate: View, position: Int){
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > getLastPositionForRemoveViewAnimation) {
            val anim = ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim.duration = 1000
            viewToAnimate.startAnimation(anim)
            getLastPositionForRemoveViewAnimation = position
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {}
            })
        }
    }

}