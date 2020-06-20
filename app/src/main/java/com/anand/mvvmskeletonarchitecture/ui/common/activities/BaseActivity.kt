package com.anand.mvvmskeletonarchitecture.ui.common.activities

import androidx.appcompat.app.AppCompatActivity
import com.anand.mvvmskeletonarchitecture.common.CustomApplication
import com.anand.mvvmskeletonarchitecture.common.dependencyinjection.PresentationCompositionRoot

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mPresentationCompositionRoot: PresentationCompositionRoot

    fun getCompositionRoot(): PresentationCompositionRoot {
        if (!::mPresentationCompositionRoot.isInitialized) {
            mPresentationCompositionRoot =
                PresentationCompositionRoot(getAppCompositionRoot(), this)
        }
        return mPresentationCompositionRoot
    }

    private fun getAppCompositionRoot() = (application as CustomApplication).getCompositionRoot()
}