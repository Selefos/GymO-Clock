package com.gym.o.gymoclock.functionality.workout_pr.user_adapter

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.functionality.calendar_pr.database.CalendarDB
import com.gym.o.gymoclock.functionality.workout_pr.database.WorkoutDB
import com.gym.o.gymoclock.functionality.workout_pr.edit_workout.*
import com.gym.o.gymoclock.utils.DateTimeUtils
import java.util.*
import kotlin.properties.Delegates


class UserAddActivityAdapter(var context: Context, val mRecyclerViewInterface: RecyclerViewInterface, val dataList: ArrayList<Elements>) :
    RecyclerView.Adapter<UserAddActivityAdapter.ViewHolder>() {


    private lateinit var workoutDB: WorkoutDB
    lateinit var calendarDB: CalendarDB
    private lateinit var db: SQLiteDatabase
    lateinit var dateTimeUtils: DateTimeUtils

    lateinit var res: Resources

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var exerciseName: TextView = itemView.findViewById(R.id.exercise_name)
        var exerciseClock: TextView = itemView.findViewById(R.id.countdown_work)
        var restClock: TextView = itemView.findViewById(R.id.countdown_rest)

        var exerciseImg: ImageButton = itemView.findViewById(R.id.exercise_img)
        var restImg: ImageButton = itemView.findViewById(R.id.rest_img)
        val removeView: ImageButton = itemView.findViewById(R.id.remove_view)

        var mwTimerIsRunning by Delegates.notNull<Boolean>()
        var mwTimerIsPaused by Delegates.notNull<Boolean>()
        var mrTimerIsRunning by Delegates.notNull<Boolean>()
        var mrTimerIsPaused by Delegates.notNull<Boolean>()

        init {
            res = itemView.context.resources
            val playPause: ImageButton = itemView.findViewById(R.id.play_pause)
            playPause.setOnClickListener {

            }

            val editView: ImageButton = itemView.findViewById(R.id.edit_view)
            editView.setOnClickListener { mRecyclerViewInterface.editExercise(adapterPosition) }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.add_view, parent, false)
        return ViewHolder(v)
    }

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

//        holder.exerciseImg.setOnClickListener {
//            if (newList.wTimerIsRunning) pauseExerciseTimer(position)
//            else {
//                if (holder.exerciseClock.text.toString() == "00:00")
//                    Toast.makeText(
//                        context,
//                        "Position: $position uninitialized timer",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                else {
//                    startExerciseTimer(position)
//                    Toast.makeText(context, "Position: $position START", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        }
//        holder.restImg.setOnClickListener {
//            if (newList.rTimerIsRunning) pauseRestTimer(position)
//            else {
//                if (holder.restClock.text.toString() == "00:00")
//                    Toast.makeText(
//                        context,
//                        "Position: $position uninitialized timer",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                else {
//                    if (newList.wTimerIsPaused)
//                        startRestTimer(position)
//                    else
//                        Toast.makeText(
//                            context,
//                            "Position: $position workout ongoing",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                }
//            }
//        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun totalTime(rounds: Int): Int {

        var totalTime = 0
        workoutDB = WorkoutDB(context)
        db = workoutDB.readableDatabase
        val cursor: Cursor = workoutDB.loadRecyclerElements(workoutName, db)

        if (cursor.moveToFirst()) {
            do {
                totalTime += cursor.getInt(2)
                totalTime += cursor.getInt(3)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return totalTime * rounds
    }

    fun totalWorkingTime(rounds: Int): Int {
        var totalTime = 0
        workoutDB = WorkoutDB(context)
        db = workoutDB.readableDatabase
        val cursor: Cursor = workoutDB.loadRecyclerElements(workoutName, db)

        if (cursor.moveToFirst()) {
            do {
                totalTime += cursor.getInt(2)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return totalTime * rounds
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

    override fun onViewDetachedFromWindow(holder: ViewHolder) {


//       if(totalTime(rounds) == 0) {
//            tts.stop()
//            tts.shutdown()
//            Log.d("RecyclerAdapter", "TTS SHUTDOWN")
//        }
        super.onViewDetachedFromWindow(holder)
    }
}