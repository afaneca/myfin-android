package com.afaneca.myfin.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.view.isVisible

/**
 * Starts a new Activity
 * @param activity the class of the activity to be started
 */
fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

/**
 * Changes the visibility (VISIBLE or GONE) the view
 * @param isVisible should visibility be VISIBLE or GONE
 */
fun View.visible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * Enables/Disables the view
 * @param shouldBeEnabled should isEnabled be TRUE or FALSE
 */
fun View.enable(shouldBeEnabled: Boolean) {
    isEnabled = shouldBeEnabled
    alpha = if (shouldBeEnabled) 1f else 0.5f
}