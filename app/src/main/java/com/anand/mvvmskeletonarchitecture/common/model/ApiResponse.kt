package com.anand.mvvmskeletonarchitecture.common.model

/**
 * Base response class for all API response. I have used template for now, but it should be changed
 * as per generic response returned by API you are consuming in your project
 * @author Anand Shinde
 */
data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val apiInteractionId: String,
    val response: T, // This encapsulates actual response you are expecting from API
    val errorInfo: ArrayList<ApiError>
)

/**
 * Error class holding error info in case API have thrown errors
 * @author Anand Shinde
 */
data class ApiError(
    val errorCode: Int,
    val message: String
)