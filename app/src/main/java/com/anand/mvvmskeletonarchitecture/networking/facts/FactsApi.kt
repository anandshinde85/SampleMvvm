package com.anand.mvvmskeletonarchitecture.networking.facts

import retrofit2.Call
import retrofit2.http.GET

const val FACTS_URL = "s/2iodh4vg0eortkl/facts.json"

interface FactsApi {
    @GET(FACTS_URL)
    fun getFacts(): Call<FactsResponse>
}