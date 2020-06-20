package com.anand.mvvmskeletonarchitecture.repository.facts

import com.anand.mvvmskeletonarchitecture.common.util.Either
import com.anand.mvvmskeletonarchitecture.networking.facts.FactsNetworkDataSource
import com.anand.mvvmskeletonarchitecture.networking.facts.FactsResponse
import com.anand.mvvmskeletonarchitecture.repository.Failure
import com.anand.mvvmskeletonarchitecture.repository.Repository
import com.anand.mvvmskeletonarchitecture.storage.DatabaseDataSource

class FactsRepository(private val factsNetworkDataSource: FactsNetworkDataSource,
                      apiBaseUrl: String, databaseDS: DatabaseDataSource
) : Repository(apiBaseUrl, databaseDS){
    suspend fun getFacts(
        forceRefresh: Boolean = false,
        onResult: suspend (Either<Failure, FactsResponse>) -> Unit
    ) {
        val key = FactsResponse.createCacheKey(apiBaseUrl)
        fetchData(key, { factsNetworkDataSource.getFacts() }, forceRefresh, onResult)
    }
}