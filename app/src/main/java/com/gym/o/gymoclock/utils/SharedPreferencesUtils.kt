package com.gym.o.gymoclock.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.NumberPicker
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName

class SharedPreferencesUtils() {

    companion object {
        private lateinit var sharedPreferences: SharedPreferences
        fun saveRoundsValueToPreferences(context: Context, scrollView: NumberPicker) {
            val sharedPreferences = context.getSharedPreferences("Rounds", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putInt("roundsInt", scrollView.value)
            save.apply()
        }

        fun getRoundsValueFromPreferences(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences("Rounds", Context.MODE_PRIVATE)
            return sharedPreferences.getInt("roundsInt", -1)
        }

        fun saveWorkoutTableNameToPreferences(context: Context, workoutName: String) {
            workoutTableName = FormatUtils.stringSpaceToUnderscore(workoutName)
            sharedPreferences = context.getSharedPreferences("WorkoutTableName", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putString("workoutName", FormatUtils.stringSpaceToUnderscore(workoutName))
            save.apply()
        }

        fun getWorkoutTableNameFromPreferences(context: Context): String {
            sharedPreferences = context.getSharedPreferences("WorkoutTableName", Context.MODE_PRIVATE)
            return sharedPreferences.getString("workoutName", "")!!
        }

        fun savePrepareTimeToPreferences(context: Context, prepareTime: Long){
            val sharedPreferences = context.getSharedPreferences("PrepareTime", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putLong("prepareTimeLong", prepareTime)
            save.apply()
        }

        fun getPrepareTimeFromPreferences(context: Context): Long{
            sharedPreferences = context.getSharedPreferences("PrepareTime", Context.MODE_PRIVATE)
            return sharedPreferences.getLong("prepareTimeLong", 0)
        }

    }
}