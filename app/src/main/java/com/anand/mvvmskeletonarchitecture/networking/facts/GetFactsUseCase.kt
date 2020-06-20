package com.anand.mvvmskeletonarchitecture.networking.facts

import com.anand.mvvmskeletonarchitecture.common.usecase.ResilienceUseCase
import com.anand.mvvmskeletonarchitecture.common.util.Either
import com.anand.mvvmskeletonarchitecture.repository.Failure
import com.anand.mvvmskeletonarchitecture.repository.facts.FactsRepository

class GetFactsUseCase(val factsRepo: FactsRepository) : ResilienceUseCase<FactsResponse, Unit>() {
    override suspend fun run(params: Unit, forceRefresh: Boolean) {
        factsRepo.getFacts(forceRefresh, ::updateData)
    }

    /**
     * Updating data as we have to remove invalid items from facts response returned by API
     */
    override suspend fun updateData(data: Either<Failure, FactsResponse?>) {
        if (data.isRight) {
            data.right()?.let {
                val factsResponse = removeInvalidData(it)
                // Setting expired cache data flag depending upon facts response
                super.updateData(Either.Right(factsResponse, it.isLongCacheData()))
            }
        } else super.updateData(data)
    }

    private fun removeInvalidData(factsResponse: FactsResponse): FactsResponse {
        val rows = factsResponse.rows.filter { row ->
            !row.title.isNullOrBlank() && !row.description.isNullOrBlank() && !row.imageHref.isNullOrBlank()
        }
        return FactsResponse(factsResponse.title, rows)
    }
}