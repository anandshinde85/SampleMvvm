package com.anand.mvvmskeletonarchitecture.common.dependencyinjection

import androidx.appcompat.app.AppCompatActivity
import com.anand.mvvmskeletonarchitecture.ui.common.viewmodel.ViewModelFactory

/**
 * Class used for injecting dependencies at views i.e. activity and fragments
 */
class PresentationCompositionRoot(
    private val mCompositionRoot: CompositionRoot,
    private val mActivity: AppCompatActivity
) {

    private fun getActivity() = mActivity

    private fun getContext() = mActivity

    private fun getFragmentManager() = getActivity().supportFragmentManager

    fun getFactsApi() = mCompositionRoot.getNetworkingCompositionRoot().getFactsApi()

    fun getAppEventBus() = mCompositionRoot.getAppEventBus()

    fun getViewModelFactory() =
        ViewModelFactory(mCompositionRoot.getNetworkingCompositionRoot().getFactsUseCase())

    fun getFactsRepository() = mCompositionRoot.getDatabaseCompositionRoot().getFactsRepository()

    fun getFactsNetworkDataSource() =
        mCompositionRoot.getDatabaseCompositionRoot().getFactsNetworkDataSource()

    fun getFactsBaseApiUrl() = mCompositionRoot.getNetworkingCompositionRoot().getFactsBaseApiUrl()

    fun getFactsUseCase() = mCompositionRoot.getNetworkingCompositionRoot().getFactsUseCase()

    fun getApiCacheDao() = mCompositionRoot.getDatabaseCompositionRoot().getApiCacheDao()

    fun getSharedPreferences() = mCompositionRoot.getSharedPreference()
}