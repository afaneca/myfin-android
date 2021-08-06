package com.afaneca.myfin.utils.charts

import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

/**
 * Created by me on 07/08/2021
 */
class PieChartPercentageValueFormatter : PercentFormatter() {
    companion object {
        private val DO_NOT_SHOW_LABEL_THRESHOLD: Float = 5F
    }


    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return if (value < DO_NOT_SHOW_LABEL_THRESHOLD) "" else getFormattedValue(value)
    }
}