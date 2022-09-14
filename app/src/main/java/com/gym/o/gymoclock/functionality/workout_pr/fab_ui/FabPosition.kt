package com.gym.o.gymoclock.functionality.workout_pr.fab_ui

import android.view.Gravity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gym.o.gymoclock.ui.workout.WorkoutFragment
import com.gym.o.gymoclock.utils.SharedPreferencesUtils

fun WorkoutFragment.setFabPosition() {
    val playPauseFABX = SharedPreferencesUtils.getPlayPauseFABPositionX(requireContext())
    val playPauseFABY = SharedPreferencesUtils.getPlayPauseFABPositionY(requireContext())

    if (playPauseFABX != 0f && playPauseFABY != 0f)
        numericalFABPosition(
            binding.playPauseFab,
            playPauseFABX,
            playPauseFABY
        )
    else
        (binding.playPauseFab.layoutParams as CoordinatorLayout.LayoutParams).gravity = Gravity.BOTTOM or Gravity.START

    val addLayoutFABX = SharedPreferencesUtils.getAddLayoutFABPositionX(requireContext())
    val addLayoutFABY = SharedPreferencesUtils.getAddLayoutFABPositionY(requireContext())

    if (addLayoutFABX != 0f && addLayoutFABY != 0f)
        numericalFABPosition(
            binding.addLayoutFab,
            addLayoutFABX,
            addLayoutFABY
        )
    else
        (binding.addLayoutFab.layoutParams as CoordinatorLayout.LayoutParams).gravity = Gravity.BOTTOM or Gravity.END

}

fun numericalFABPosition(fab: FloatingActionButton, x: Float, y: Float) {
    fab.x = x
    fab.y = y
}