package com.gym.o.gymoclock.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.NumberPicker
import com.gym.o.gymoclock.functionality.workout_pr.workoutTableName

class SharedPreferencesUtils {

    companion object {

        private lateinit var sharedPreferences: SharedPreferences


        fun saveRoundsValueToPreferences(context: Context, scrollView: NumberPicker) {
            val sharedPreferences = context.getSharedPreferences("Rounds", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putInt("rounds", scrollView.value)
            save.apply()
        }

        fun getRoundsValueFromPreferences(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences("Rounds", Context.MODE_PRIVATE)
            return sharedPreferences.getInt("rounds", -1)
        }


        fun saveWorkoutTableNameToPreferences(context: Context, workoutName: String) {
            workoutTableName = FormatUtils.stringSpaceToUnderscore(workoutName)
            sharedPreferences = context.getSharedPreferences("WorkoutTableName", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putString("workoutTableName", FormatUtils.stringSpaceToUnderscore(workoutName))
            save.apply()
        }

        fun getWorkoutTableNameFromPreferences(context: Context): String {
            sharedPreferences = context.getSharedPreferences("WorkoutTableName", Context.MODE_PRIVATE)
            return sharedPreferences.getString("workoutTableName", "")!!
        }


        fun savePrepareTimeToPreferences(context: Context, prepareTime: Long) {
            val sharedPreferences = context.getSharedPreferences("PrepareTime", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putLong("prepareTime", prepareTime)
            save.apply()
        }

        fun getPrepareTimeFromPreferences(context: Context): Long {
            sharedPreferences = context.getSharedPreferences("PrepareTime", Context.MODE_PRIVATE)
            return sharedPreferences.getLong("prepareTime", 0)
        }


        fun saveTtsState(context: Context, prefTtsState: Boolean) {
            val sharedPreferences = context.getSharedPreferences("TtsState", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putBoolean("ttsState", prefTtsState)
            save.apply()
        }

        fun getTtsState(context: Context): Boolean {
            sharedPreferences = context.getSharedPreferences("TtsState", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("ttsState", false)
        }


        fun saveVolume(context: Context, volume: Int) {
            val sharedPreferences = context.getSharedPreferences("Volume", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putInt("volume", volume)
            save.apply()
        }

        fun getVolume(context: Context): Int {
            sharedPreferences = context.getSharedPreferences("Volume", Context.MODE_PRIVATE)
            return sharedPreferences.getInt("volume", -1)
        }


        fun saveAllAnimationsState(context: Context, prefAllAnimationsState: Boolean) {
            val sharedPreferences = context.getSharedPreferences("AllAnimationsState", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putBoolean("allAnimationsState", prefAllAnimationsState)
            save.apply()
        }

        fun getAllAnimationsState(context: Context): Boolean {
            sharedPreferences = context.getSharedPreferences("AllAnimationsState", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("allAnimationsState", false)
        }


        fun saveLayoutAnimationsState(context: Context, prefRecyclerViewAnimationsState: Boolean) {
            val sharedPreferences = context.getSharedPreferences("RecyclerViewAnimationsState", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putBoolean("recyclerViewAnimationsState", prefRecyclerViewAnimationsState)
            save.apply()
        }

        fun getLayoutAnimationsState(context: Context): Boolean {
            sharedPreferences = context.getSharedPreferences("RecyclerViewAnimationsState", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("recyclerViewAnimationsState", false)
        }


        fun saveClocksAnimationsState(context: Context, prefClocksAnimationsState: Boolean) {
            val sharedPreferences = context.getSharedPreferences("ClocksAnimationsState", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putBoolean("clocksAnimationsState", prefClocksAnimationsState)
            save.apply()
        }

        fun getClocksAnimationsState(context: Context): Boolean {
            sharedPreferences = context.getSharedPreferences("ClocksAnimationsState", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("clocksAnimationsState", false)
        }


        fun savePlayPauseFABPositionXY(context: Context, x: Float, y: Float) {
            val sharedPreferences = context.getSharedPreferences("PlayPauseFABPositionXY", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putFloat("playPauseFABPositionX", x)
            save.putFloat("playPauseFABPositionY", y)
            save.apply()
        }

        fun getPlayPauseFABPositionX(context: Context): Float {
            sharedPreferences = context.getSharedPreferences("PlayPauseFABPositionXY", Context.MODE_PRIVATE)
            return sharedPreferences.getFloat("playPauseFABPositionX", 0.0f)
        }

        fun getPlayPauseFABPositionY(context: Context): Float {
            sharedPreferences = context.getSharedPreferences("PlayPauseFABPositionXY", Context.MODE_PRIVATE)
            return sharedPreferences.getFloat("playPauseFABPositionY", 0.0f)
        }


        fun saveAddLayoutFABPositionXY(context: Context, x: Float, y: Float) {
            val sharedPreferences = context.getSharedPreferences("AddLayoutFABPositionXY", Context.MODE_PRIVATE)
            val save = sharedPreferences.edit()
            save.putFloat("addLayoutFABPositionX", x)
            save.putFloat("addLayoutFABPositionY", y)
            save.apply()
        }

        fun getAddLayoutFABPositionX(context: Context): Float {
            sharedPreferences = context.getSharedPreferences("AddLayoutFABPositionXY", Context.MODE_PRIVATE)
            return sharedPreferences.getFloat("addLayoutFABPositionX", 0.0f)
        }

        fun getAddLayoutFABPositionY(context: Context): Float {
            sharedPreferences = context.getSharedPreferences("AddLayoutFABPositionXY", Context.MODE_PRIVATE)
            return sharedPreferences.getFloat("addLayoutFABPositionY", 0.0f)
        }
    }

}