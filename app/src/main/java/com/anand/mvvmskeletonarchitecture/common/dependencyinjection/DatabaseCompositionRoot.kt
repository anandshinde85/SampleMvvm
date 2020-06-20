package com.anand.mvvmskeletonarchitecture.common.dependencyinjection

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.anand.mvvmskeletonarchitecture.common.util.DateFormat
import com.anand.mvvmskeletonarchitecture.common.util.EncryptionUtil
import com.anand.mvvmskeletonarchitecture.common.util.GsonUTCDateAdapter
import com.anand.mvvmskeletonarchitecture.networking.facts.FactsNetworkDataSource
import com.anand.mvvmskeletonarchitecture.repository.facts.FactsRepository
import com.anand.mvvmskeletonarchitecture.storage.BaseDatabase
import com.anand.mvvmskeletonarchitecture.storage.DatabaseDataSource
import com.anand.mvvmskeletonarchitecture.storage.database.AppDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.security.KeyStore
import java.security.PrivateKey
import java.util.*

class DatabaseCompositionRoot(
    private val context: Context,
    private val encryptionUtil: EncryptionUtil,
    private val sharedPreferences: SharedPreferences,
    private val networkingCompositionRoot: NetworkingCompositionRoot
) {
    private lateinit var gson: Gson

    fun getDatabase(): BaseDatabase {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            AppDatabase.getInstance(
                context,
                sharedPreferences,
                (encryptionUtil.getKey() as PrivateKey).toString()
            )
        } else {
            AppDatabase.getInstance(
                context,
                sharedPreferences,
                (encryptionUtil.getKey() as KeyStore.PrivateKeyEntry).toString()
            )
        }
    }

    fun getGson(): Gson {
        if (!::gson.isInitialized) {
            gson = GsonBuilder()
                .setDateFormat(DateFormat.ZULU.it)
                .registerTypeAdapter(Date::class.java, GsonUTCDateAdapter())
                .setLenient()
                .create()
        }
        return gson
    }

    fun getFactsRepository() = FactsRepository(
        getFactsNetworkDataSource(),
        networkingCompositionRoot.getFactsBaseApiUrl(),
        DatabaseDataSource(getGson(), getDatabase())
    )

    fun getFactsNetworkDataSource() =
        FactsNetworkDataSource(networkingCompositionRoot.getFactsApi(), context)

    fun getApiCacheDao() = getDatabase().apiCacheDao()
}