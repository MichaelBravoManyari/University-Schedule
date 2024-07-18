package com.studentsapps.ui.timetable

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

fun animateOpacity(view: View, toAlpha: Float, onAnimationEnd: (() -> Unit)? = null) {
    view.animate()
        .alpha(toAlpha)
        .setDuration(200)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            onAnimationEnd?.invoke()
        }
        .start()
}