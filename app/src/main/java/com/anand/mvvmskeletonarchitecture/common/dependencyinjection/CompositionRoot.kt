package com.anand.mvvmskeletonarchitecture.common.dependencyinjection

import android.content.Context
import android.content.SharedPreferences
import com.anand.mvvmskeletonarchitecture.common.model.AppEventBus
import com.anand.mvvmskeletonarchitecture.common.util.EncryptionUtil

/**
 * Root class for injecting dependencies at application level
 */
class CompositionRoot(
    private val context: Context,
    private val encryptionUtil: EncryptionUtil,
    private val sharedPreferences: SharedPreferences
) {
    private lateinit var appEventBus: AppEventBus
    private lateinit var networkingCompositionRoot: NetworkingCompositionRoot
    private lateinit var databaseCompositionRoot: DatabaseCompositionRoot

    fun getContext() = context

    fun getSharedPreference() = sharedPreferences

    fun getAppEventBus(): AppEventBus {
        if (!::appEventBus.isInitialized) {
            appEventBus = AppEventBus()
        }
        return appEventBus
    }

    fun getNetworkingCompositionRoot(): NetworkingCompositionRoot {
        if (!::networkingCompositionRoot.isInitialized) {
            networkingCompositionRoot = NetworkingCompositionRoot(this)
        }
        return networkingCompositionRoot
    }

    fun getDatabaseCompositionRoot(): DatabaseCompositionRoot {
        if (!::databaseCompositionRoot.isInitialized) {
            databaseCompositionRoot = DatabaseCompositionRoot(
                context,
                encryptionUtil,
                sharedPreferences,
                getNetworkingCompositionRoot()
            )
        }
        return databaseCompositionRoot
    }
}