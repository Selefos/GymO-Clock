package com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.databases.WorkoutDB
import com.gym.o.gymoclock.functionality.workout_pr.animations.exerciseSettingsButtonAnimationControl
import com.gym.o.gymoclock.functionality.workout_pr.animations.setOnAddViewAnimation
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName
import com.gym.o.gymoclock.interfaces.RecyclerViewInterface
import com.gym.o.gymoclock.utils.FormatUtils
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
        val editView: ImageButton = itemView.findViewById(R.id.edit_view)

        var mwTimerIsRunning by Delegates.notNull<Boolean>()
        var mwTimerIsPaused by Delegates.notNull<Boolean>()
        var mrTimerIsRunning by Delegates.notNull<Boolean>()
        var mrTimerIsPaused by Delegates.notNull<Boolean>()

        var weightEditText: EditText = itemView.findViewById(R.id.weight_edit)

        var repsEditText: EditText = itemView.findViewById(R.id.reps_edit)

        var isSettingsVisible = false

        init {
            resources = itemView.context.resources

            FormatUtils.textChangeListenerDecimal(context, weightEditText, null, null)
            weightEditText.filters += InputFilter.LengthFilter(7) // set max length

            FormatUtils.textChangeListenerInteger(context, repsEditText, null, null)
            repsEditText.filters += InputFilter.LengthFilter(3) // set max length
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.add_recycler_view, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //holder.setIsRecyclable(false)
        //if(!holderList.containsKey(holder.adapterPosition))
        holderList[holder.adapterPosition] = holder

        val newList = dataList[position]
        holder.exerciseName.text = newList.exerciseNameValue

        holder.exerciseClock.text = newList.exerciseClockValue.text
        holder.mwTimerIsRunning = newList.wTimerIsRunning
        holder.mwTimerIsPaused = newList.wTimerIsPaused

        holder.restClock.text = newList.restClockValue.text
        holder.mrTimerIsRunning = newList.rTimerIsRunning
        holder.mrTimerIsPaused = newList.rTimerIsPaused


        if (SharedPreferencesUtils.getLayoutAnimationsState(context))
            setOnAddViewAnimation(holder.itemView, position)

        holder.exerciseSettingsButton.setOnClickListener {
            holder.isSettingsVisible = !holder.isSettingsVisible

            exerciseSettingsButtonAnimationControl(context,
                holder.isSettingsVisible, holder.exerciseSettingsButton,
                holder.editView, holder.removeView)
        }

        holder.editView.setOnClickListener { mRecyclerViewInterface.editExercise(holder.adapterPosition) }

        holder.removeView.setOnClickListener {
            mRecyclerViewInterface.removeExercise(holder.itemView, holder.adapterPosition)
        }


    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
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