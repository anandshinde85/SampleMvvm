package com.anand.mvvmskeletonarchitecture.common.util

import com.anand.mvvmskeletonarchitecture.repository.Failure

sealed class Resource<T> {
    // loading state
    class Loading<T> : Resource<T>()

    // base success state which holds a value
    open class BaseLoaded<T>(val data: T) : Resource<T>()

    // different success states
    // this state is emitted when
    // 1) data is in the short cache
    // 2) API call response comes successfully
    class Loaded<T>(data: T) : BaseLoaded<T>(data)

    // this state is emitted when the data is present in the long cache and API call is being made in the background
    class LoadedWithApiInProgress<T>(data: T) : BaseLoaded<T>(data)

    // base failure state which holds a failure
    open class BaseFailed<T>(val exception: Failure) : Resource<T>()

    // different failed states
    // this state is emitted when the network call fails but there is data in the long cache
    class FailedWithCacheData<T>(exception: Failure) : BaseFailed<T>(exception)

    // this state is emitted when the network call fails and there is no data in the cache
    class Failed<T>(exception: Failure) : BaseFailed<T>(exception)
}