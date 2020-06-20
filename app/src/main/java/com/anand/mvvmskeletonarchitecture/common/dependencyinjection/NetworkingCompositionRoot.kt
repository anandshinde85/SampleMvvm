package com.anand.mvvmskeletonarchitecture.common.dependencyinjection

import com.anand.mvvmskeletonarchitecture.networking.facts.FactsApi
import com.anand.mvvmskeletonarchitecture.networking.facts.GetFactsUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val FACTS_BASE_URL = "https://dl.dropboxusercontent.com/"
class NetworkingCompositionRoot(private val compositionRoot: CompositionRoot) {
    private lateinit var factsRetrofit: Retrofit

    private fun getFactsRetrofit(): Retrofit {
        if (!::factsRetrofit.isInitialized) {
            // Create OkHttpBuilder
            val okHttpBuilder = OkHttpClient.Builder()
            okHttpBuilder.connectTimeout(30, TimeUnit.SECONDS)
            okHttpBuilder.readTimeout(30, TimeUnit.SECONDS)

            // Create HTTPLogggingInterceptor
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpBuilder.addInterceptor(loggingInterceptor)

            // Create OkHttpClient
            val okHttpClient = okHttpBuilder.build()
            factsRetrofit = Retrofit.Builder()
                .baseUrl(FACTS_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return factsRetrofit
    }

    fun getFactsBaseApiUrl() = FACTS_BASE_URL

    fun getFactsApi() = getFactsRetrofit().create(FactsApi::class.java)

    fun getFactsUseCase() = GetFactsUseCase(compositionRoot.getDatabaseCompositionRoot().getFactsRepository())
}