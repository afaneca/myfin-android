package com.afaneca.myfin.utils

import android.animation.ObjectAnimator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.afaneca.myfin.R

/**
 * Created by me on 03/07/2021
 */

fun setupAmountStyle(type: String, textView: TextView) {
    TextViewCompat.setTextAppearance(
        textView,
        when (type) {
            MyFinConstants.MYFIN_TRX_TYPE.INCOME.value -> R.style.AmountTypeCredit
            MyFinConstants.MYFIN_TRX_TYPE.EXPENSE.value -> R.style.AmountTypeDebit
            else -> R.style.AmountTypeTransfer
        }
    )
}

fun setupBalanceStyle(amount: Double, textView: TextView) {
    TextViewCompat.setTextAppearance(
        textView,
        when {
            amount > 0 -> R.style.AmountTypeCredit
            amount == 0.toDouble() -> R.style.AmountTypeTransfer
            else -> R.style.AmountTypeDebit
        }
    )
}

fun setProgressBarValueWithAnimation(
    progressBarView: ProgressBar,
    newProgress: Int,
    animationDuration: Long = 500
) {
    ObjectAnimator.ofInt(progressBarView, "progress", newProgress)
        .setDuration(animationDuration)
        .start()
}
