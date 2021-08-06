package com.afaneca.myfin.utils.charts

import android.content.Context
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.afaneca.myfin.R
import com.afaneca.myfin.utils.formatMoney
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

/**
 * Created by me on 06/08/2021
 */
open class PieChartMarkerView constructor(
    context: Context,
    @LayoutRes layoutRes: Int = R.layout.pie_charts_marker_view
) : MarkerView(context, layoutRes) {
    private var valueTv: TextView
    private var labelTv: TextView

    init {
        valueTv = findViewById(R.id.value_tv)
        labelTv = findViewById(R.id.label_tv)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), -height.toFloat())
        /* return super.getOffset()*/
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {

        e?.let {
            if (e is PieEntry) {
                valueTv.text = formatMoney(e.y.toDouble())
                labelTv.text = e.label
            }
        }
        super.refreshContent(e, highlight)
    }
}