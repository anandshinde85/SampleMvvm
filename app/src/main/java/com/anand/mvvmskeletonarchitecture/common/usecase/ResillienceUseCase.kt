package com.anand.mvvmskeletonarchitecture.common.usecase

import androidx.lifecycle.LiveData
import com.anand.mvvmskeletonarchitecture.common.util.Either
import com.anand.mvvmskeletonarchitecture.common.util.SingleLiveEvent
import com.anand.mvvmskeletonarchitecture.repository.Failure
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/*
    This is to be used when we have an API which needs resilience implemented

    This usecase has a livedata which will be observed by the viewmodels to get the updates pushed from
    repository layer.
 */
abstract class ResilienceUseCase<Type, in Params> : CoroutineScope {

    private val liveData = SingleLiveEvent<Either<Failure, Type?>>()

    val data: LiveData<Either<Failure, Type?>>
        get() = liveData

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    abstract suspend fun run(params: Params, forceRefresh: Boolean = false)

    @Synchronized
    operator fun invoke(
        params: Params,
        forceRefresh: Boolean = false
    ) {
        launch(dispatcher) {
            run(params, forceRefresh)
        }
    }

    /**
     * This method updates the livedata with the data pushed from repository level
     * Override this method only when you want to do some operations on data returned by API
     * Also do not forget to call this method only after processing data and most importantly
     * do not forget to use and attach data.isLongCacheData() for Right/success response
     */
    protected open suspend fun updateData(data: Either<Failure, Type?>) {
        liveData.postValue(data)
    }

    // cancel the job in progress
    fun cancel() {
        liveData.clear()
        job.cancel()
    }

    companion object {
        lateinit var dispatcher: CoroutineDispatcher
    }
}