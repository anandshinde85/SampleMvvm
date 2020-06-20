package com.anand.mvvmskeletonarchitecture.repository

import com.anand.mvvmskeletonarchitecture.common.util.DateUtil

const val ONE_SECOND: Long = 1000
const val ONE_MINUTE: Long = ONE_SECOND * 60
const val TWO_MINUTES: Long = ONE_MINUTE * 2
const val ONE_HOUR: Long = ONE_MINUTE * 60
const val ONE_DAY: Long = ONE_HOUR * 24
const val TWO_DAYS: Long = ONE_DAY * 2
const val ONE_WEEK: Long = ONE_DAY * 7

enum class CacheTime {
    SHORT,
    LONG
}

abstract class CacheableResponse {

    abstract val shortCacheTime: Long
    abstract val longCacheTime: Long

    var dateCreatedTimeStamp: Long = 0

    var forceRefresh = false

    fun isCacheExpired(cacheTime: CacheTime): Boolean {
        return if (CacheTime.SHORT == cacheTime)
            DateUtil.isExpired(dateCreatedTimeStamp, shortCacheTime)
        else
            DateUtil.isExpired(dateCreatedTimeStamp, longCacheTime)
    }

    fun isLongCacheData(): Boolean {
        return isCacheExpired(CacheTime.SHORT) && !isCacheExpired(
            CacheTime.LONG
        )
    }

    fun isShortCacheData(): Boolean {
        return !isCacheExpired(CacheTime.SHORT)
    }
}