package com.gym.o.gymoclock.functionality.workout_pr.animations

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.gym.o.gymoclock.functionality.workout_pr.getLastPositionForAddViewAnimation
import com.gym.o.gymoclock.functionality.workout_pr.getLastPositionForRemoveViewAnimation
import com.gym.o.gymoclock.functionality.workout_pr.recycler_adapter.ExerciseRecyclerAdapter

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

fun ExerciseRecyclerAdapter.setOnRemoveViewAnimation(viewToAnimate: View, position: Int) {
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

fun scalePosButtonAnimation(viewToAnimate: View){
    val anim = ScaleAnimation(
        0.0f, 1.0f, 0.0f, 1.0f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.duration = 2000
    viewToAnimate.startAnimation(anim)
}

fun scaleNegButtonAnimation(viewToAnimate: View){
    val anim = ScaleAnimation(
        1.0f, 0.0f, 1.0f, 0.0f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.duration = 2000
    viewToAnimate.startAnimation(anim)
}

fun exerciseSettingsAnimation(viewToAnimate: View){
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
                duration = 1500
                scaleX(1f)
                scaleY(1f)
            }.start()
        }, 700)
}