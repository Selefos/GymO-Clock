package com.gym.o.gymoclock.functionality.workout_pr.countdown_functionality

import android.animation.ObjectAnimator
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar

fun ProgressBar.setBigMax(max: Int) {
    this.max = max * 1000
}

lateinit var objectAnimator: ObjectAnimator
fun ProgressBar.animateTo(progressTo: Int, startDelay: Long, animationDuration: Long) {
    objectAnimator = ObjectAnimator.ofInt(
        this,
        "progress",
        this.progress,
        progressTo * 1000
    )
    objectAnimator.duration = animationDuration
    objectAnimator.interpolator = LinearInterpolator()
    objectAnimator.startDelay = startDelay
    objectAnimator.start()
}

fun ProgressBar.pauseAnimation() {
    objectAnimator.pause()
    Log.i("Animation", "Animation Paused")
}