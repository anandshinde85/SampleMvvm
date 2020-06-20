package com.anand.mvvmskeletonarchitecture.networking.facts

import android.content.Context
import com.anand.mvvmskeletonarchitecture.common.util.Either
import com.anand.mvvmskeletonarchitecture.repository.Failure
import com.anand.mvvmskeletonarchitecture.repository.NetworkDataSource

class FactsNetworkDataSource(private val factsApi: FactsApi, context: Context) :
    NetworkDataSource(context) {
    fun getFacts(): Either<Failure, FactsResponse> = request(factsApi.getFacts())
}