package com.anand.mvvmskeletonarchitecture.repository

import com.anand.mvvmskeletonarchitecture.common.model.ApiError

/**
 * Base Class for handling errors/failures/exceptions.
 * Customised failure can be created by extending  [CustomFailure] class.
 *
 * @author Anand Shinde
 */
sealed class Failure(
    var msg: String? = "",
    val error: Exception = Exception(),
    var isCachedDataPresent: Boolean = false
) : Exception(msg, error) {
    class NetworkConnection : Failure()
    class Network404Error : Failure()
    class DatabaseError : Failure()

    /** * Extend this class for some specific customised failures.*/
    abstract class CustomFailure : Failure()

    fun getTitle(): String = this.javaClass.simpleName
    override fun toString(): String = this.javaClass.name

    class ServerError(msg: String?, error: Exception) : Failure(msg, error) {
        var apiErrors: Array<ApiError> = emptyArray()
        var statusCode = 0

        constructor(apiResponseErrors: Array<ApiError>, msg: String, statusCode: Int = 0) : this(
            msg,
            Exception()
        ) {
            this.statusCode = statusCode
            this.apiErrors = apiResponseErrors
        }
    }
}