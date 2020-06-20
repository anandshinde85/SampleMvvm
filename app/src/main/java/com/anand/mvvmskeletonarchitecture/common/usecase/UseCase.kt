package com.anand.mvvmskeletonarchitecture.common.usecase

import com.anand.mvvmskeletonarchitecture.common.util.Either
import com.anand.mvvmskeletonarchitecture.repository.Failure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/* Contract for all use cases */
abstract class UseCase<out Type : Any, in Params> : CoroutineScope {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Main + job

    abstract suspend fun run(params: Params, forceRefresh: Boolean = false): Either<Failure, Type?>
    operator fun invoke(
        params: Params,
        forceRefresh: Boolean = false,
        onResult: (Either<Failure, Type?>) -> Unit = {}
    ) {
        if (job.isCancelled) job = Job()
        launch {
            val result = withContext(dispatcher) {
                run(params, forceRefresh)
            }
            onResult(result)
        }
    }

    // cancel the job in progress
    fun cancel() {
        job.cancel()
    }

    companion object {
        lateinit var dispatcher: CoroutineDispatcher
    }
}