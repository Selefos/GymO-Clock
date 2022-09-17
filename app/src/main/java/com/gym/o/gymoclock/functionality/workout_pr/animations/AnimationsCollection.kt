package com.gym.o.gymoclock.functionality.workout_pr.animations

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gym.o.gymoclock.R
import com.gym.o.gymoclock.enums.ClockSelected
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality.isExerciseAnimating
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality.isRestAnimating
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality.restTimeInMillis
import com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality.workTimeInMillis
import com.gym.o.gymoclock.functionality.workout_pr.getLastPositionForAddViewAnimation
import com.gym.o.gymoclock.functionality.workout_pr.getLastPositionForRemoveViewAnimation
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseRecyclerAdapter
import com.gym.o.gymoclock.utils.SharedPreferencesUtils

fun setOnAddViewAnimation(viewToAnimate: View, position: Int) {
    // If the bound view wasn't previously displayed on screen, it's animated
    if (position > getLastPositionForAddViewAnimation) {
        val anim = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim.duration = 1000
        viewToAnimate.startAnimation(anim)
        getLastPositionForAddViewAnimation = position
    }
}

fun setOnRemoveViewAnimation(viewToAnimate: View, position: Int) {
    // If the bound view wasn't previously displayed on screen, it's animated
    if (position > getLastPositionForRemoveViewAnimation) {
        val anim = ScaleAnimation(
            1.0f, 0.0f, 1.0f, 0.0f,
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

fun scalePosButtonAnimation(viewToAnimate: View) {
    val anim = ScaleAnimation(
        0.0f, 1.0f, 0.0f, 1.0f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.duration = 2000
    viewToAnimate.startAnimation(anim)
}

fun scaleNegButtonAnimation(viewToAnimate: View) {
    val anim = ScaleAnimation(
        1.0f, 0.0f, 1.0f, 0.0f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.duration = 2000
    viewToAnimate.startAnimation(anim)
}

fun exerciseSettingsButtonAnimation(viewToAnimate: View) {
    //rotate
    Handler(Looper.getMainLooper()).postDelayed(
        {
            viewToAnimate.animate().apply {
                duration = 2000
                rotationBy(720f)
            }.start()
        }, 0)

    //shrink
    Handler(Looper.getMainLooper()).postDelayed(
        {
            viewToAnimate.animate().apply {
                duration = 300
                scaleX(0.1f)
                scaleY(0.1f)
            }.start()
        }, 500)

    //enlarge
    Handler(Looper.getMainLooper()).postDelayed(
        {
            viewToAnimate.animate().apply {
                duration = 1400
                scaleX(1f)
                scaleY(1f)
            }.start()
        }, 700)

}

fun ExerciseRecyclerAdapter.exerciseClockAnimate() {
    if (isExerciseAnimating)
        mRecyclerViewInterface.startClockProgressBar(ClockSelected.clockSelected, workTimeInMillis)

    isExerciseAnimating = false
}

fun ExerciseRecyclerAdapter.restClockAnimate() {
    if (isRestAnimating)
        mRecyclerViewInterface.startClockProgressBar(ClockSelected.clockSelected, restTimeInMillis)

    isRestAnimating = false
}

fun exerciseSettingsButtonAnimationControl(context: Context, isSettingsVisible: Boolean, exerciseSettingsButton: ImageButton,
    editView: ImageButton, removeView: ImageButton) {

    if (isSettingsVisible)
        unfoldSettingsAnimation(context, exerciseSettingsButton, editView, removeView)
    else
        foldSettingsAnimation(context, exerciseSettingsButton, editView, removeView)
}

fun unfoldSettingsAnimation(context: Context, exerciseSettingsButton: ImageButton, editView: ImageButton, removeView: ImageButton) {

    if (SharedPreferencesUtils.getLayoutAnimationsState(context)) {
        exerciseSettingsButtonAnimation(exerciseSettingsButton)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                exerciseSettingsButton.background = AppCompatResources.getDrawable(context, R.drawable.ic_exercise_settings_button_cirlces)
            }, 700)
        scalePosButtonAnimation(editView)
        scalePosButtonAnimation(removeView)

    } else
        exerciseSettingsButton.background = AppCompatResources.getDrawable(context, R.drawable.ic_exercise_settings_button_cirlces)

    editView.isVisible = true
    removeView.isVisible = true
}

fun foldSettingsAnimation(context: Context, exerciseSettingsButton: ImageButton, editView: ImageButton, removeView: ImageButton) {
    if (SharedPreferencesUtils.getLayoutAnimationsState(context)) {
        exerciseSettingsButtonAnimation(exerciseSettingsButton)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                exerciseSettingsButton.background = AppCompatResources.getDrawable(context, R.drawable.ic_exercise_settings_button_borders)
            }, 700)

        scaleNegButtonAnimation(editView)
        scaleNegButtonAnimation(removeView)

    } else
        exerciseSettingsButton.background = AppCompatResources.getDrawable(context, R.drawable.ic_exercise_settings_button_borders)

    editView.isVisible = false
    removeView.isVisible = false
}

fun fabFadeInFadeOutAnimation(fab: FloatingActionButton) {

    Handler(Looper.getMainLooper()).postDelayed(
        {
            fab.animate().alpha(0f).setDuration(500).start()
        }, 20)

    Handler(Looper.getMainLooper()).postDelayed(
        {
            fab.animate().alpha(1f).setDuration(500).start()
        }, 1020)

}

fun fabFadeOutFadeInAnimation(fab: FloatingActionButton) {

    Handler(Looper.getMainLooper()).postDelayed(
        {
            fab.animate().alpha(0f).setDuration(1000).start()
        }, 0)

    Handler(Looper.getMainLooper()).postDelayed(
        {
            fab.animate().alpha(1f).setDuration(1000).start()
        }, 1000)

}