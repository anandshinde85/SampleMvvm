package com.anand.mvvmskeletonarchitecture.common

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.anand.mvvmskeletonarchitecture.common.dependencyinjection.CompositionRoot
import com.anand.mvvmskeletonarchitecture.common.usecase.ResilienceUseCase
import com.anand.mvvmskeletonarchitecture.common.usecase.UseCase
import com.anand.mvvmskeletonarchitecture.common.util.EncryptionUtil
import kotlinx.coroutines.Dispatchers.IO

class CustomApplication : Application() {

    private lateinit var mCompositionRoot: CompositionRoot
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var encryptionUtil: EncryptionUtil

    override fun onCreate() {
        super.onCreate()
        UseCase.dispatcher = IO
        ResilienceUseCase.dispatcher = IO
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        encryptionUtil = EncryptionUtil(applicationContext)
        mCompositionRoot = CompositionRoot(applicationContext, encryptionUtil, sharedPreferences)
    }

    fun getSharedPreferences() = sharedPreferences

    fun getEncryptionUtil() = encryptionUtil

    fun getCompositionRoot() = mCompositionRoot
}