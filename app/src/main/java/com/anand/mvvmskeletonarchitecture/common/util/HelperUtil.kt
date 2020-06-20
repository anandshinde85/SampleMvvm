package com.anand.mvvmskeletonarchitecture.common.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

object HelperUtil {
    val gson: Gson = GsonBuilder()
        .setDateFormat(DateFormat.ZULU.it)
        .registerTypeAdapter(Date::class.java, GsonUTCDateAdapter())
        .setLenient()
        .create()
}