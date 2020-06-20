package com.anand.mvvmskeletonarchitecture.storage

import androidx.room.*

@Dao
@TypeConverters(DateConverter::class)
interface ApiCacheDao {

    @Query("SELECT * FROM $API_CACHE_TABLE_NAME where key = :key")
    fun findByKey(key: String): ApiCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(apiCache: ApiCache)

    @Query("DELETE FROM $API_CACHE_TABLE_NAME")
    fun delete()

    @Query("DELETE FROM $API_CACHE_TABLE_NAME where key = :key")
    fun deleteByKey(key: String)

    @Query("DELETE FROM $API_CACHE_TABLE_NAME WHERE key LIKE :key")
    fun deleteCampaignCache(key: String)
}