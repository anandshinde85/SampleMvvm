package com.anand.mvvmskeletonarchitecture.common.util

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val EMPTY = ""
const val ZERO_DST_OFFSET = 0
const val BRISBANE_TIME_ZONE_ID = "Australia/Brisbane"
const val TIME_24H = "HH:mm"
const val TIME_12H = "h:mma"
const val UTC = "UTC"

object DateUtil {

    fun getContractEndDate(date: Date?): String {
        return date?.let { d ->
            try {
                val outputFormat =
                    SimpleDateFormat(DateFormat.SERVICES_DAY_MONTH_YEAR.it, Locale.ENGLISH)
                outputFormat.format(d)
            } catch (dateException: ParseException) {
                Log.e("ParseException", "Failed to parse the supplied date.", dateException)
                EMPTY
            }
        } ?: EMPTY
    }

    /***
     * Formats date in time slot format.
     * @startDate ex: 2017-11-01T08:00:00Z
     * @endDate ex: 2017-11-01T12:00:00Z
     * @return Wednesday 1 Nov, 07pm - 11pm
     */
    fun getSlotFormattedDate(
        dateFormatArg: DateFormat = DateFormat.SLOT_DATE_FORMAT,
        startDate: Date,
        endDate: Date
    ): String {
        return try {
            val dateFormat = SimpleDateFormat(dateFormatArg.it)
            val start = dateFormat.format(startDate)
            val timeFormat = SimpleDateFormat(DateFormat.SLOT_TIME_FORMAT.it)
            val startTime = timeFormat.format(startDate)
            val endTime = timeFormat.format(endDate)

            "$start, $startTime - $endTime"
        } catch (dateException: ParseException) {
            Log.e("ParseException", "" + dateException)
            EMPTY
        }
    }

    fun getFormattedDate(date: Date, format: DateFormat, removeTime: Boolean = false): String {
        return try {
            val outputFormat = SimpleDateFormat(format.it)
            outputFormat.format(if (removeTime) removeTime(date) else date).replace(".", "")
        } catch (dateException: ParseException) {
            Log.e("ParseException", "" + dateException)
            EMPTY
        }
    }

    /**
     * Returns formatted date without any timeZone offset
     */
    fun formatDate(date: Date, format: DateFormat): String {
        return try {
            val outputFormat = SimpleDateFormat(format.it, Locale.getDefault())
            outputFormat.format(date)
        } catch (e: ParseException) {
            EMPTY
        }
    }

    fun isExpired(dateCreatedTimeStamp: Long, timeElapsed: Long): Boolean {
        // TODO migrate to new datetime APIs once available in support or our min ver is upgraded to 26
        val current = Date(dateCreatedTimeStamp + timeElapsed)
        return Date().after(current)
    }

    fun daysRemaining(due: Date): Long {
        val today = Calendar.getInstance().time
        val diffInMillies = due.time - today.time
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }

    /**
     * @return true if the supplied date is today else false
     */
    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val specifiedDate = Calendar.getInstance()
        specifiedDate.time = date

        return (today.get(Calendar.DAY_OF_MONTH) == specifiedDate.get(Calendar.DAY_OF_MONTH) &&
                today.get(Calendar.MONTH) == specifiedDate.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == specifiedDate.get(Calendar.YEAR))
    }

    fun changeCalenderMonth(cal: Calendar, count: Int, isIncrease: Boolean) {
        return if (isIncrease)
            cal.add(Calendar.MONTH, count)
        else
            cal.add(Calendar.MONTH, -count)
    }

    /**
     * Accepts 24hr time and returns it's 12hr version (am/pm)
     * ex: 13:00 gives 1:00pm
     */
    fun get12HourFormat(time: String): String {
        return try {
            val format24Hr = SimpleDateFormat(TIME_24H, Locale.getDefault())
            val format12Hr = SimpleDateFormat(TIME_12H, Locale.getDefault())
            val format24HrDate = format24Hr.parse(time)
            format12Hr.format(format24HrDate)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Accepts Date and returns only the time component.
     *
     * by default 12h-format time in local timezone is returned
     */
    fun getTime(date: Date, format: String = TIME_12H): String {
        val timeFormat = SimpleDateFormat(format, Locale.getDefault())
        return timeFormat.format(date).toLowerCase(Locale.ENGLISH)
    }

    fun isSameDay(day1: Long, day2: Long): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.timeInMillis = day1
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = day2
        return calendar1.get(Calendar.ERA) == calendar2.get(Calendar.ERA) &&
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Get date object for given date and time (string)
     */
    fun getDate(date: Date, time12Hr: String): Date {
        val standardDate = getFormattedDate(date, DateFormat.dd_MM_yyyy)
        val combinedFormat = SimpleDateFormat("dd-MM-yyyy h:mma", Locale.getDefault())
        return combinedFormat.parse("$standardDate $time12Hr")
    }

    /**
     * Get date of the month in number
     */
    fun getDateOfTheMonth(date: Date): String {
        val today = Calendar.getInstance()
        today.time = date
        return today.get(Calendar.DAY_OF_MONTH).toString()
    }

    /**
     * Get past date for n days
     */
    fun getDateDaysAgo(cal: Calendar, daysCount: Int): Date {
        cal.add(Calendar.DAY_OF_MONTH, daysCount)
        return cal.time
    }

    /**
     * Get past date for n days
     */
    fun getDateDaysAgo(
        date: Date,
        daysCount: Int,
        isBrisbaneTimeZone: Boolean = true,
        removeTime: Boolean = false
    ): Date {
        val today: Calendar
        if (isBrisbaneTimeZone) {
            today = Calendar.getInstance(TimeZone.getTimeZone(BRISBANE_TIME_ZONE_ID))
        } else {
            today = Calendar.getInstance()
        }
        if (removeTime) removeTime(date)
        today.time = if (removeTime) removeTime(date) else date
        today.add(Calendar.DAY_OF_MONTH, daysCount)
        return today.time
    }

    /**
     * AEST time returned by service will remain at +10 only for Brisbane, so using that timezone instead of Sydney time
     */
    fun format(
        dateToFormat: Date,
        pattern: String = DateFormat.BACK_SLASH_DATE_FORMAT.it,
        timeZone: TimeZone = TimeZone.getTimeZone(BRISBANE_TIME_ZONE_ID)
    ): String? {
        val format = SimpleDateFormat(pattern)
        format.timeZone = timeZone
        return format.format(dateToFormat)
    }

    fun getBrisbaneCurrentDate(): Date = Calendar.getInstance(TimeZone.getTimeZone(BRISBANE_TIME_ZONE_ID)).time

    fun getDateWithBrisbaneTimeZone(date: Date): Date {
        val formattedDate = format(date)
        val format = SimpleDateFormat(DateFormat.BACK_SLASH_DATE_FORMAT.it)
        val dateWithBrisbaneTimezone: Date
        try {
            dateWithBrisbaneTimezone = format.parse(formattedDate)
        } catch (e: ParseException) {
            Log.e("ParseException", "Error in parsing Brisbane date", e)
            return Date()
        }

        return dateWithBrisbaneTimezone
    }

    fun getDaysWithoutTimeBetween(startDate: Date, endDate: Date): Long {
        val startDateClone = Calendar.getInstance()
        startDateClone.time = removeTime(startDate)
        val startDateDSTOffset = startDateClone.get(Calendar.DST_OFFSET)

        val endDateClone = Calendar.getInstance()
        endDateClone.time = removeTime(endDate)
        val endDateDSTOffset = endDateClone.get(Calendar.DST_OFFSET)

        val dstOffset = endDateDSTOffset - startDateDSTOffset
        val diff =
            endDateClone.timeInMillis - startDateClone.timeInMillis + if (dstOffset > ZERO_DST_OFFSET) dstOffset else ZERO_DST_OFFSET

        return diff / TimeUnit.DAYS.toMillis(1)
    }

    fun getDaysBetween(date: Date, future: Date): Long {
        return TimeUnit.DAYS.convert(future.time - date.time, TimeUnit.MILLISECONDS)
    }

    fun removeTime(date: Date): Date {
        val dateClone = Calendar.getInstance()
        dateClone.time = date
        dateClone.set(Calendar.AM_PM, Calendar.AM)
        dateClone.set(Calendar.HOUR, 0)
        dateClone.set(Calendar.MINUTE, 0)
        dateClone.set(Calendar.SECOND, 0)
        dateClone.set(Calendar.MILLISECOND, 0)
        return dateClone.time
    }
}

enum class DateFormat(val it: String) {
    ZULU("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    ZULU_WITH_MILLISECONDS("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"),
    WITH_TIMEZONE("MM-dd-yyyy'T'HH:mm:ssXX"),
    DAY_MONTH_YEAR("d MMM yyyy"),
    MILESTONE_DATE_FORMAT("EEEE, d MMM yyyy"),
    STORE_DATE_FORMAT("d MMM yyyy (EEE)"),
    SLOT_DATE_FORMAT("EEEE d MMM"),
    SLOT_TIME_FORMAT("ha"),
    BILL_BAR_DATE_FORMAT("MMM"),
    BACK_SLASH_DATE_FORMAT("dd/MM/yyyy"),
    dd_MM_yyyy("dd-MM-yyyy"),
    DAY_FORMAT("EEEE"),
    DATE_FORMAT("d"),
    MONTH_FORMAT("MMM"),
    YEAR_FORMAT("yyyy"),
    TIME_FORMAT("hh aa"),
    DAY_MONTH("d MMM"),
    TCOM_SERVICES_DATE_FORMAT("yyyy-MM-dd'T'HH:mm:ssZ"),
    SERVICES_DAY_MONTH_YEAR("dd MMM yyyy"),
    SHORT_WEEK_DAY_MONTH_FORMAT("EEE d MMM")
}