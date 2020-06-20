package com.anand.mvvmskeletonarchitecture.storage

import androidx.room.*

const val API_CACHE_TABLE_NAME = "api_cache"

@Entity(tableName = API_CACHE_TABLE_NAME)
@TypeConverters(DateConverter::class)
data class ApiCache(
    @PrimaryKey
    val key: String,
    val value: String,
    val type: String = "",
    val forceRefresh: Boolean = false
)