package com.kuikly.init.common.base.platform

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

actual class NetworkMonitor(private val app: Application) {
    actual fun isConnected(): Boolean {
        val cm = app.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    actual fun getNetworkType(): NetworkType {
        val cm = app.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return NetworkType.NONE
        val caps = cm.getNetworkCapabilities(network) ?: return NetworkType.NONE
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            else -> NetworkType.NONE
        }
    }
}

actual fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor(
    AppContext.androidContext as? Application
        ?: throw IllegalStateException("AppContext not initialized")
)
