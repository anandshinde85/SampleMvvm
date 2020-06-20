package com.anand.mvvmskeletonarchitecture.common.util

import android.util.Log
import com.google.gson.*
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import com.anand.mvvmskeletonarchitecture.common.util.DateFormat.ZULU
import com.anand.mvvmskeletonarchitecture.common.util.DateFormat.ZULU_WITH_MILLISECONDS
import com.anand.mvvmskeletonarchitecture.common.util.DateFormat.WITH_TIMEZONE
import java.util.*

/**
 * By default Gson does not support UTC and uses the local timezone to parse Date.
 * This class adds UTC support to the serializer.
 *
 * https://code.google.com/p/google-gson/issues/detail?id=281
 */
class GsonUTCDateAdapter : JsonSerializer<Date?>,
    JsonDeserializer<Date> {

    companion object {
        private const val TAG = "GsonUTCDateAdapter"
    }

    private val supportedDateFormats = arrayOf(ZULU.it, ZULU_WITH_MILLISECONDS.it, WITH_TIMEZONE.it)

    @Synchronized
    override fun serialize(
        date: Date?,
        type: Type,
        jsonSerializationContext: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(getDateFormat(ZULU.it).format(date))
    }

    private fun getDateFormat(format: String): DateFormat {
        return SimpleDateFormat(format, Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone(UTC)
        }
    }

    @Synchronized
    override fun deserialize(
        jsonElement: JsonElement,
        type: Type,
        jsonDeserializationContext: JsonDeserializationContext

    ): Date? {
        for (format in supportedDateFormats) {
            try {
                return getDateFormat(format).parse(jsonElement.asString)
            } catch (e: ParseException) {
            }
        }
        Log.d(
            TAG,
            "Unparseable date: ${jsonElement.asString}. Supported formats: ${supportedDateFormats.contentToString()}"
        )
        return null
    }
}