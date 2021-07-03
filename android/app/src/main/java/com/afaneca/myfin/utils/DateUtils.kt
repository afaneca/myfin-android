package com.afaneca.myfin.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by me on 26/06/2021
 */
class DateUtils {

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
    }
}