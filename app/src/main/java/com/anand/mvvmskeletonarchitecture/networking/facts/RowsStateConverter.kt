package com.anand.mvvmskeletonarchitecture.networking.facts

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class RowsStateConverter {

    @TypeConverter
    fun convertStringToRows(data: String?): List<Rows?>? {
        val gson = Gson()
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<Rows?>?>() {}.type
        return gson.fromJson<List<Rows?>>(data, listType)
    }

    @TypeConverter
    fun convertRowsToString(rows: List<Rows?>?): String? {
        val gson = Gson()
        return gson.toJson(rows)
    }
}