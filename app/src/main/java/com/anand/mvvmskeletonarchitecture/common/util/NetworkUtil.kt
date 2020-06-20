package com.anand.mvvmskeletonarchitecture.common.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import java.util.HashMap


object NetworkUtil {
    val TAG = javaClass.name
    val RETRY_TIMES = 3

    /**
     * Function to create and return cache key with supplied params
     * @param url: End point of API
     * @param map: params associated with API
     */
    fun updateUrlWithMap(url: String, map: HashMap<String, String>): String {
        var updatedUrl = url
        var queryParameters = ""

        map.entries.forEach {
            if (updatedUrl.contains("{${it.key}}")) {
                updatedUrl = updatedUrl.replace("{${it.key}}", it.value, true)
            } else {
                queryParameters += it.key + "=" + it.value + "&"
            }
        }

        if (queryParameters.isNotEmpty() && queryParameters.endsWith("&")) {
            queryParameters = "?${queryParameters.substring(0, queryParameters.length - 1)}"
        }

        return updatedUrl + queryParameters
    }

    fun isConnected(app: Context?): Boolean {
        return app?.let { isConnectedToWifi(it) || isConnectedToMobile(it) } ?: false
    }

    fun isConnectedToMobile(app: Context) = isConnected(app, ConnectionType.MOBILE)

    fun isConnectedToWifi(app: Context) = isConnected(app, ConnectionType.WIFI)

    fun isDefaultConnectedToMobileNetwork(app: Context): Boolean {
        return (isConnectedToMobile(app) && isMobileDataOn(app)) && !isConnectedToWifi(app)
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
    }

    private fun isConnected(app: Context, type: ConnectionType): Boolean {
        try {
            val connectionType = getConnectionType(app)
            val isConnected = getConnectedState(app)
            return (connectionType == type && isConnected)
        } catch (ex: Exception) {
            // getting null pointer exception on some devices while trying to getActiveNetworkInfo();
            // happening from NetworkBroadcastReceiver
            Log.e(TAG, ex.toString())
        }

        return false
    }

    @Suppress("DEPRECATION")
    private fun getConnectionType(app: Context): ConnectionType {
        var result = ConnectionType.NONE
        val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                getNetworkCapabilities(activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = ConnectionType.WIFI
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = ConnectionType.MOBILE
                    }
                }
            }
        } else {
            cm?.run {
                activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = ConnectionType.WIFI
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = ConnectionType.MOBILE
                    }
                }
            }
        }
        return result
    }

    fun isDisconnectedAfterRetry(app: Context) =
        !isConnected(app) && !retryNetworkConnectionCheck(app)

    fun retryNetworkConnectionCheck(app: Context): Boolean {
        Log.w(TAG, "NOT connected to network")
        var isConnected = false
        for (i in 0 until RETRY_TIMES) {
            try {
                Thread.sleep(1000)
                isConnected = isConnected(app)
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            if (isConnected) {
                Log.w("NetworkUtil", "We are now connected to network")
                break
            }
        }
        return isConnected
    }

    private fun getMcc(context: Context): Int {
        (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperator?.run {
            return try {
                Integer.parseInt(substring(0, 3))
            } catch (e: Exception) {
                0
            }
        }
        return 0
    }

    private fun getConnectedState(app: Context) =
        (app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo.isConnected

    internal enum class ConnectionType {
        NONE, MOBILE, WIFI
    }

    fun isMobileDataOn(app: Context): Boolean {
        var mobileDataEnabled = false // Assume disabled
        val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            val cmClass = Class.forName(cm.javaClass.name)
            val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
            method.isAccessible = true // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = method.invoke(cm) as Boolean
        } catch (e: Exception) {
            Log.e(
                "NetworkUtil",
                "Not able to access private method getMobileDataEnabled() from connectivityManager ",
                e
            )
        }
        return mobileDataEnabled
    }
}