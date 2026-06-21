package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val readableSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)
    private val dayAbbrSdf = SimpleDateFormat("EEE", Locale.US)
    private val dayNumSdf = SimpleDateFormat("d", Locale.US)

    fun getTodayString(): String {
        return sdf.format(Date())
    }

    fun parseDate(dateStr: String): Date? {
        return try {
            sdf.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(date: Date): String {
        return sdf.format(date)
    }

    fun formatReadable(dateStr: String): String {
        val date = parseDate(dateStr) ?: return dateStr
        return readableSdf.format(date)
    }

    fun getDayOfWeekAbbreviation(dateStr: String): String {
        val date = parseDate(dateStr) ?: return "MON"
        return dayAbbrSdf.format(date).uppercase(Locale.US)
    }

    fun getDayOfMonthNumber(dateStr: String): String {
        val date = parseDate(dateStr) ?: return "1"
        return dayNumSdf.format(date)
    }

    fun getDaysDifference(startStr: String, currentStr: String): Int {
        val start = parseDate(startStr) ?: return 0
        val current = parseDate(currentStr) ?: return 0
        val diffMs = current.time - start.time
        val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()
        return diffDays
    }

    fun addDays(dateStr: String, days: Int): String {
        val date = parseDate(dateStr) ?: Date()
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, days)
        return sdf.format(cal.time)
    }

    fun getDayOfWeeksName(dateStr: String): String {
        val date = parseDate(dateStr) ?: return "Monday"
        val cal = Calendar.getInstance()
        cal.time = date
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            Calendar.SUNDAY -> "Sunday"
            else -> "Monday"
        }
    }
}
