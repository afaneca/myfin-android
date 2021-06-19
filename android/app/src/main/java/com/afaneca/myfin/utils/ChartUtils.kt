package com.afaneca.myfin.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.afaneca.myfin.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet

/**
 * Created by me on 13/06/2021
 */
class ChartUtils {
    companion object {
        fun buildHalfPieChart(
            context: Context,
            pieChart: PieChart,
            title: String,
            dataset: PieData
        ): PieChart {
            with(pieChart) {
                centerText = title
                setUsePercentValues(true)
                isDrawHoleEnabled = true
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBackground
                    )
                )
                setHoleColor(ContextCompat.getColor(context, R.color.colorBackground))
                //setTransparentCircleColor(Color.WHITE)
                setTransparentCircleAlpha(50)
                holeRadius = 64f
                transparentCircleRadius = 30f
                setDrawCenterText(true)
                isRotationEnabled = false
                isHighlightPerTapEnabled = false
                maxAngle = 180f
                rotationAngle = 180f
                setCenterTextOffset(0F, -20F)
                animateY(1400, Easing.EaseInOutQuad)
                legend.isEnabled = false
                description.isEnabled = false
                // entry label styling
                //setEntryLabelColor(Color.WHITE)
                //setEntryLabelTypeface(tfRegular)
                //setEntryLabelTextSize(12f)
            }

            pieChart.data = dataset
            pieChart.setExtraOffsets(0f, -200f, 0f, -300f)
            pieChart.minOffset = 0f

            pieChart.invalidate()

            return pieChart
        }
    }
}