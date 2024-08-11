package com.dhkim.common

import android.annotation.SuppressLint
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
object DateUtil {

    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    @SuppressLint("SimpleDateFormat")
    fun millsToDate(mills: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = sdf.format(Timestamp(mills))

        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun dateToMills(date: String): Long {
        val realDate = try {
            sdf.parse(date)
        } catch (e: Exception) {
            null
        }

        return realDate?.time ?: 0L
    }

    fun getDateGap(newDate: String): Long {
        return try {
            val afterDate = convertStringToDate(newDate)
            val gap= TimeUnit.MINUTES.convert(
                afterDate!!.time - Calendar.getInstance().time.time,
                TimeUnit.MILLISECONDS
            ).toDouble() / 1440
            if (gap > 0 && gap <1) {
                1
            } else if (gap <= 0){
                0
            } else {
                gap.toLong() + 1
            }
        } catch (e: Exception) {
            -1
        }
    }

    fun convertStringToDate(strDate: String, pattern: String = "yyyy-MM-dd"): Date? {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            format.parse(strDate)
        } catch (e: Exception) {
            null
        }
    }

    fun currentTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val now = Date()

        return sdf.format(now)
    }

    fun isAfter(strDate: String): Boolean {
        val today = convertStringToDate(todayDate())
        val date = convertStringToDate(strDate)

        return (today?.after(date) ?: false) || strDate == todayDate()
    }

    fun isBefore(strDate: String): Boolean {
        val today = convertStringToDate(todayDate())
        val date = convertStringToDate(strDate)

        return (today?.before(date) ?: false) || strDate == todayDate()
    }

    fun isAfter(strDate1: String, strDate2: String): Boolean {
        val date1 = convertStringToDate(strDate1)
        val date2 = convertStringToDate(strDate2)

        return (date1?.after(date2) ?: false) || date1 == date2
    }

    fun isBefore(strDate1: String, strDate2: String): Boolean {
        val date1 = convertStringToDate(strDate1)
        val date2 = convertStringToDate(strDate2)

        return (date1?.before(date2) ?: false) || date1 == date2
    }

    fun todayDate(): String {
        val calendar: Calendar = Calendar.getInstance()
        return sdf.format(calendar.time)
    }

    fun dateAfterYears(years: Int): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, years)

        return sdf.format(calendar.time)
    }

    fun dateAfterMonths(months: Int): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, months)

        return sdf.format(calendar.time)
    }

    fun dateAfterDays(days: Int): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, days)

        return sdf.format(calendar.time)
    }
}