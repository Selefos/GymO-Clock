package com.gym.o.gymoclock.interfaces

import android.view.View
import com.gym.o.gymoclock.enums.ClockSelectedEnum


interface RecyclerViewInterface {

    fun editExercise(dataPosition: Int)

    fun removeExercise(itemView: View, dataPosition: Int)

    fun startClockProgressBar(clockSelected: ClockSelectedEnum, animDuration: Long)

    fun stopClockProgressBar(clockSelected: ClockSelectedEnum)

    fun roundsCount()

    fun loadRecyclerViews()

    fun scrollToPosition()

}