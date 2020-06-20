package com.anand.mvvmskeletonarchitecture.storage

import com.anand.mvvmskeletonarchitecture.repository.CacheTime
import com.anand.mvvmskeletonarchitecture.repository.CacheableResponse
import com.google.gson.Gson

//@Singleton
//open class DatabaseDataSource @Inject constructor(val gson: Gson, val baseDatabase: BaseDatabase) {
open class DatabaseDataSource (val gson: Gson, val baseDatabase: BaseDatabase) {


    fun save(key: String, data: CacheableResponse) {
        val value = gson.toJson(data)

        val cacheData = ApiCache(
            getPrefixForKey(key),
            value,
            data.javaClass.simpleName
        )
        baseDatabase.apiCacheDao().save(cacheData)
    }

    inline fun <reified T : CacheableResponse> find(
        key: String,
        cacheTime: CacheTime = CacheTime.LONG
    ): T? {
        val cache = baseDatabase.apiCacheDao().findByKey(getPrefixForKey(key))

        if (cache != null) {
            val cachable = gson.fromJson(cache.value, T::class.java)
            cachable.forceRefresh = cache.forceRefresh
            if (cachable != null && !cachable.isCacheExpired(cacheTime))
                return cachable
        }
        return null
    }

    fun deleteByKey(key: String) {
        baseDatabase.apiCacheDao().deleteByKey(getPrefixForKey(key))
    }

    /**
     * Function to add specific prefix for given key, this is usually user name so that different user
     * in same app can have multiple cached key response
     */
    fun getPrefixForKey(key: String) = key
}