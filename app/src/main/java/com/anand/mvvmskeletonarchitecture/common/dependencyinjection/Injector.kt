package com.anand.mvvmskeletonarchitecture.common.dependencyinjection

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.anand.mvvmskeletonarchitecture.common.model.AppEventBus
import com.anand.mvvmskeletonarchitecture.networking.facts.FactsApi
import com.anand.mvvmskeletonarchitecture.networking.facts.FactsNetworkDataSource
import com.anand.mvvmskeletonarchitecture.networking.facts.GetFactsUseCase
import com.anand.mvvmskeletonarchitecture.repository.facts.FactsRepository
import com.anand.mvvmskeletonarchitecture.storage.ApiCacheDao
import com.anand.mvvmskeletonarchitecture.ui.common.viewmodel.ViewModelFactory
import java.lang.reflect.Field

class Injector(private val mPresentationCompositionRoot: PresentationCompositionRoot) {
    fun inject(client: Any) {
        var clazz: Class<*> = client.javaClass
        // Inject current class's fields
        injectAllFields(clazz, client)
        while (clazz.getSuperclass() != null) {
            // Inject super class's fields
            clazz = clazz.getSuperclass()
            injectAllFields(clazz, client)
        }
    }

    private fun injectAllFields(clazz: Class<*>, client: Any) {
        val fields = clazz.declaredFields
        for (field in fields) {
            if (isAnnotatedForInjection(field)) {
                injectField(client, field)
            }
        }
    }

    private fun isAnnotatedForInjection(field: Field): Boolean {
        val annotations = field.declaredAnnotations
        for (annotation in annotations) {
            if (annotation is Service) {
                return true
            }
        }
        return false
    }

    private fun injectField(client: Any, field: Field) {
        try {
            val isAccessibleInitially = field.isAccessible
            field.isAccessible = true
            field[client] = getServiceForClass(field.type)
            field.isAccessible = isAccessibleInitially
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    private fun getServiceForClass(type: Class<*>): Any {
        return when (type) {
            FactsApi::class.java -> {
                mPresentationCompositionRoot.getFactsApi()
            }
            AppEventBus::class.java -> {
                mPresentationCompositionRoot.getAppEventBus()
            }
            ViewModelFactory::class.java, ViewModelProvider.Factory::class.java -> {
                mPresentationCompositionRoot.getViewModelFactory()
            }
            FactsRepository::class.java -> {
                mPresentationCompositionRoot.getFactsRepository()
            }
            FactsNetworkDataSource::class.java -> {
                mPresentationCompositionRoot.getFactsNetworkDataSource()
            }
            GetFactsUseCase::class.java -> {
                mPresentationCompositionRoot.getFactsUseCase()
            }
            Named::class.java -> {
                mPresentationCompositionRoot.getFactsBaseApiUrl()
            }
            ApiCacheDao::class.java -> {
                mPresentationCompositionRoot.getApiCacheDao()
            }
            SharedPreferences::class.java -> {
                mPresentationCompositionRoot.getSharedPreferences()
            }
            else -> {
                throw RuntimeException("unsupported service type class: $type")
            }
        }
    }

}