package com.anand.mvvmskeletonarchitecture.common.viewmodels

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.anand.mvvmskeletonarchitecture.common.usecase.ResilienceUseCase
import com.anand.mvvmskeletonarchitecture.common.util.Either
import com.anand.mvvmskeletonarchitecture.common.util.Resource
import com.anand.mvvmskeletonarchitecture.common.util.SingleLiveEvent
import com.anand.mvvmskeletonarchitecture.repository.Failure

/**
                            ** Must follow **
    if load() method of the viewmodel doesn't take any parameter, make the input Type P to be Unit
    even for the usecase, if you are not making use of query, make the UseCase Params to be of type Unit.
 */
abstract class BaseViewModel<in P, T>(private val resilienceUseCase: ResilienceUseCase<T, *>? = null) : ViewModel() {

    var liveData: LiveData<Resource<T?>> = MutableLiveData()
    val singleLiveData: LiveData<Resource<T?>> = SingleLiveEvent<Resource<T?>>()
    private lateinit var useCaseLiveDataObserver: Observer<Either<Failure, T?>>

    init {
        initLiveData()
    }

    @Suppress("UNCHECKED_CAST")
    fun loadData(query: P? = null, forceRefresh: Boolean = false) {
        if (liveData.value is Resource.Loading) {
            // This is required to handle the scenario when the orientation changes and the state is loading to avoid showing blank screen
            updateLiveData(Resource.Loading())
            return
        }

        if (query == null) {
            load(Unit as P, forceRefresh)
        } else
            load(query, forceRefresh)
    }

    private fun initLiveData() {
        if (resilienceUseCase == null)
            return

        useCaseLiveDataObserver = Observer {
            handleData(it)
        }

        resilienceUseCase.data.observeForever(useCaseLiveDataObserver)
    }

    /*
        This method converts the updates sent from the live data in the usecase layer which is in the form of Either to
        Resource (something that view understands)
     */
    protected fun handleData(eitherResponse: Either<Failure, T?>) {
        when (eitherResponse) {
            is Either.Loading -> updateLiveData(Resource.Loading())

            is Either.Left -> {
                if (eitherResponse.cacheFlag)
                    updateLiveData(Resource.FailedWithCacheData(eitherResponse.left()))
                else
                    updateLiveData(Resource.Failed(eitherResponse.left()))
            }

            is Either.Right -> {
                if (eitherResponse.expiredFlag)
                    updateLiveData(Resource.LoadedWithApiInProgress(eitherResponse.right()))
                else
                    updateLiveData(Resource.Loaded(eitherResponse.right()))
            }
        }
    }

    @CallSuper
    protected open fun load(query: P, forceRefresh: Boolean = false) {
        // emit loading state only if the observable use case is null
        // else the loading state will be emitted from the repository layer depending on the caching logic
        if (resilienceUseCase == null)
            updateLiveData(Resource.Loading())
    }

    private fun updateLiveData(resourceState: Resource<T?>) {
        (liveData as MutableLiveData).value = resourceState
        (singleLiveData as SingleLiveEvent).value = resourceState
    }

    /**
     * This method clears the livaData and sets it to null
     */
    fun clearData() {
        (liveData as MutableLiveData).value = null
    }

    override fun onCleared() {
        resilienceUseCase?.cancel()
        resilienceUseCase?.data?.removeObserver(useCaseLiveDataObserver)
        super.onCleared()
    }
}