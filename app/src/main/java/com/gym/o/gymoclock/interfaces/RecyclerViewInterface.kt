package com.gym.o.gymoclock.interfaces

import android.view.View


interface RecyclerViewInterface {

    fun editExercise(dataPosition: Int)

    fun removeExercise(itemView: View, dataPosition: Int)

    fun roundsCount()

    fun loadRecyclerViews()

    fun scrollToPosition()
}