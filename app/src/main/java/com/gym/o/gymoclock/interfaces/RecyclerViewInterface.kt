package com.gym.o.gymoclock.interfaces

import android.view.View
import com.gym.o.gymoclock.enums.ClockSelectedEnum


interface RecyclerViewInterface {

    fun editExercise(dataPosition: Int)

    fun removeExercise(itemView: View, dataPosition: Int)

    fun animateClock(clockSelected: ClockSelectedEnum, animDuration: Long)

    fun stopAnimation(clockSelected: ClockSelectedEnum)

    fun roundsCount()

    fun loadRecyclerViews()

    fun scrollToPosition()

}