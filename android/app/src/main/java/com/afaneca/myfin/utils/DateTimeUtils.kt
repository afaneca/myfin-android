package com.afaneca.myfin.utils

import java.text.SimpleDateFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.*

/**
 * Created by me on 26/06/2021
 */
class DateTimeUtils {

    companion object {
        fun getDayOfMonthFromDate(date: Date): String {
            val sdf = SimpleDateFormat("d")
            return sdf.format(date)
        }

        fun getDayOfMonthFromUnixTime(unixTime: Long): String {
            return getDayOfMonthFromDate(Date(unixTime))
        }

        fun getFullYearFromDate(date: Date): String {
            val sdf = SimpleDateFormat("Y")
            return sdf.format(date)
        }

        fun getFullYearFromUnixTime(unixTime: Long): String {
            return getFullYearFromDate(Date(unixTime))
        }

        fun getAbbreviatedWeekDayFromDate(date: Date): String {
            val sdf = SimpleDateFormat("EEE")
            return sdf.format(date)
        }

        fun getAbbreviatedWeekDayFromUnixTime(unixTime: Long): String {
            return getAbbreviatedWeekDayFromDate(Date(unixTime))
        }

        fun getAbbreviatedMonthFromDate(date: Date): String {
            val sdf = SimpleDateFormat("MMM")
            return sdf.format(date)
        }

        fun getAbbreviatedMonthFromUnixTime(unixTime: Long): String {
            return getAbbreviatedMonthFromDate(Date(unixTime))
        }

        fun getFormattedDateTimeFromUnixTime(unixTime: Long): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            return sdf.format(Date(unixTime * 1000L))
        }

        fun convertMonthIntToString(
            monthInt: Int,
            textStyle: TextStyle = TextStyle.SHORT_STANDALONE
        ): String {
            return Month.of(monthInt).getDisplayName(
                textStyle,
                Locale.getDefault()
            )
        }
    }
}