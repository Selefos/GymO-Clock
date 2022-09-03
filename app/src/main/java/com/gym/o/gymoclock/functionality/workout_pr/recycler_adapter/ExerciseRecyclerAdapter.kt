package com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.enums.PrepareTimerState
import com.gym.o.gymoclock.enums.PrepareTimerStateEnum
import com.gym.o.gymoclock.functionality.workout_pr.animations.*
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.utils.SharedPreferencesUtils
import kotlin.properties.Delegates


class ExerciseRecyclerAdapter(var context: Context, val mRecyclerViewInterface: RecyclerViewInterface,
    val dataList: ArrayList<ExerciseElements>) : RecyclerView.Adapter<ExerciseRecyclerAdapter.ViewHolder>() {

    private lateinit var workoutDB: WorkoutDB
    private lateinit var db: SQLiteDatabase
    lateinit var resources: Resources
    private var holderList: HashMap<Int, ViewHolder> = HashMap()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var exerciseSettingsButton: ImageButton = itemView.findViewById(R.id.exercise_settings_button)

        var exerciseName: TextView = itemView.findViewById(R.id.exercise_name)

        var exerciseProgress: ProgressBar = itemView.findViewById(R.id.progress_bar_work)
        var exerciseClock: TextView = itemView.findViewById(R.id.countdown_work)

        var restProgress: ProgressBar = itemView.findViewById(R.id.progress_bar_rest)
        var restClock: TextView = itemView.findViewById(R.id.countdown_rest)

        val removeView: ImageButton = itemView.findViewById(R.id.remove_view)

        var mwTimerIsRunning by Delegates.notNull<Boolean>()
        var mwTimerIsPaused by Delegates.notNull<Boolean>()
        var mrTimerIsRunning by Delegates.notNull<Boolean>()
        var mrTimerIsPaused by Delegates.notNull<Boolean>()


        init {
            resources = itemView.context.resources

            val editView: ImageButton = itemView.findViewById(R.id.edit_view)
            editView.setOnClickListener { mRecyclerViewInterface.editExercise(adapterPosition) }

            var isSettingsVisible = false
            exerciseSettingsButton.setOnClickListener{
                isSettingsVisible = !isSettingsVisible

                if (SharedPreferencesUtils.getRecyclerViewAnimationsState(context))
                    exerciseSettingsAnimation(exerciseSettingsButton)

                if(isSettingsVisible) {
                    if (SharedPreferencesUtils.getRecyclerViewAnimationsState(context)) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                exerciseSettingsButton.background = AppCompatResources.getDrawable(context, R.drawable.ic_exercise_settings_button_cirlces)
                            }, 700)
                        scalePosButtonAnimation(editView)
                        scalePosButtonAnimation(removeView)

                    }
                    else
                        exerciseSettingsButton.background = AppCompatResources.getDrawable(context, R.drawable.ic_exercise_settings_button_cirlces)

                    editView.isVisible = true
                    removeView.isVisible = true
                }
                else{
                    if (SharedPreferencesUtils.getRecyclerViewAnimationsState(context)) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                exerciseSettingsButton.background = AppCompatResources.getDrawable(context, R.drawable.ic_exercise_settings_button_borders)
                            }, 700)

                        scaleNegButtonAnimation(editView)
                        scaleNegButtonAnimation(removeView)

                    }
                    else
                        exerciseSettingsButton.background = AppCompatResources.getDrawable(context, R.drawable.ic_exercise_settings_button_borders)

                    editView.isVisible = false
                    removeView.isVisible = false
                }

            }
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

        //holder.setIsRecyclable(false)
        //if(!holderList.containsKey(holder.adapterPosition))
        holderList[holder.adapterPosition] = holder

        if (SharedPreferencesUtils.getRecyclerViewAnimationsState(context))
            setOnAddViewAnimation(holder.itemView, position)

        holder.removeView.setOnClickListener {
            mRecyclerViewInterface.removeExercise(holder.itemView, holder.adapterPosition)
        }
    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    fun totalTimeFromDB(rounds: Int): Int {
        workoutDB = WorkoutDB(context)
        val totalTime = workoutDB.totalTimeFromWorkoutDB(workoutTableName, rounds)
        return totalTime.toInt()
    }


    fun totalWorkingTime(rounds: Int): Int {
        var totalTime = 0
        workoutDB = WorkoutDB(context)
        db = workoutDB.readableDatabase
        val cursor: Cursor = workoutDB.loadRecyclerElements(workoutTableName, db)

        if (cursor.moveToFirst()) {
            do {
                totalTime += cursor.getInt(2)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return totalTime * rounds
    }


    fun getViewByPosition(position: Int): ExerciseRecyclerAdapter.ViewHolder? {
        return holderList[position]
    }

}