package com.afaneca.myfin.utils.charts

import android.content.Context
import androidx.core.content.ContextCompat
import com.afaneca.myfin.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*

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


        fun buildPieChart(
            context: Context,
            pieChart: PieChart,
            title: String,
            dataset: Map<String, Double>,
            isLegendEnabled: Boolean = true
        ) {
            var pieEntryList = ArrayList<PieEntry>()
            for (item in dataset) {
                pieEntryList.add(PieEntry(item.value.toFloat(), item.key))
            }
            val pieDataSet: PieDataSet =
                PieDataSet(pieEntryList, title).apply {
                    setColors(
                        *ColorTemplate.MATERIAL_COLORS,
                        *ColorTemplate.LIBERTY_COLORS,
                        *ColorTemplate.COLORFUL_COLORS,
                        *ColorTemplate.JOYFUL_COLORS,
                        *ColorTemplate.PASTEL_COLORS,
                        *ColorTemplate.VORDIPLOM_COLORS
                    )
                    /*setDrawValues(false)*/
                    valueTextSize = 10f
                    valueFormatter = PieChartPercentageValueFormatter()
                }

            pieChart.apply {
                /*description = Description().apply { text = "Distribuição das Despesas" }*/
                setUsePercentValues(true)
                centerText = title
                setCenterTextColor(context.getColor(R.color.colorOnPrimary))
                setHoleColor(context.getColor(android.R.color.transparent))
                isDrawHoleEnabled = true
                isRotationEnabled = true
                data = PieData(pieDataSet)
                description.isEnabled = false
                legend.isEnabled = isLegendEnabled
                legend.textColor = context.getColor(R.color.colorOnPrimary)
                setDrawEntryLabels(false)
                setDrawEntryLabels(false)
                holeRadius = 64F
                /*setEntryLabelTextSize(context.resources.getDimension(R.dimen.default_title_text_size))*/
                /*setUsePercentValues(true)*/
            }
            pieChart.marker = PieChartMarkerView(context)
            pieChart.highlightValues(null)
            pieChart.invalidate()
            pieChart.animateXY(500, 500)
        }
    }
}