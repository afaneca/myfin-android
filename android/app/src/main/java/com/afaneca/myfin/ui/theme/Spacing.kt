package com.afaneca.myfin.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data object SpacingDefaults {
    internal const val TINY = 2
    internal const val EXTRA_SMALL = 4
    internal const val SMALL = 8
    internal const val MEDIUM = 16
    internal const val LARGE = 24
    internal const val LARGER1 = 32
    internal const val LARGER2 = 42
    internal const val LARGER3 = 48
    internal const val LARGEST = 56
    internal const val DEFAULT = SMALL
}

data class Spacing(
    val default: Dp = SpacingDefaults.DEFAULT.dp,
    val tiny: Dp = SpacingDefaults.TINY.dp,
    val extraSmall: Dp = SpacingDefaults.EXTRA_SMALL.dp,
    val small: Dp = SpacingDefaults.SMALL.dp,
    val medium: Dp = SpacingDefaults.MEDIUM.dp,
    val large: Dp = SpacingDefaults.LARGE.dp,
    val larger1: Dp = SpacingDefaults.LARGER1.dp,
    val larger2: Dp = SpacingDefaults.LARGER2.dp,
    val larger3: Dp = SpacingDefaults.LARGER3.dp,
    val largest: Dp = SpacingDefaults.LARGEST.dp,
)