package com.gym.o.gymoclock.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.NumberPicker

class SharedPreferencesUtils(context: Context) {
    private lateinit var sharedPreferences: SharedPreferences
    private var context: Context? = null

    init {
        this.context = context
    }

    fun saveRoundsValueToPreferences(scrollView: NumberPicker) {
        val sharedPreferences = context!!.getSharedPreferences("Rounds", Context.MODE_PRIVATE)
        val save = sharedPreferences.edit()
        save.putInt("roundsInt", scrollView.value)
        save.apply()
    }

    fun getRoundsValueFromPreferences(): Int {
        val sharedPreferences = context!!.getSharedPreferences("Rounds", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("roundsInt", -1)
    }

    fun saveWorkoutTableNameToPreferences(workoutName: String) {
        sharedPreferences = context!!.getSharedPreferences("WorkoutTableName", Context.MODE_PRIVATE)
        val save = sharedPreferences.edit()
        save.putString("workoutName", workoutName)
        save.apply()
    }

    fun getWorkoutTableNameFromPreferences(): String {
        sharedPreferences = context!!.getSharedPreferences("WorkoutTableName", Context.MODE_PRIVATE)
        return sharedPreferences.getString("workoutName", "")!!
    }

}