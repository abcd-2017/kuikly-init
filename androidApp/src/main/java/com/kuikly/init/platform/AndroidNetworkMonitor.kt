package com.kuikly.init.platform

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.kuikly.init.common.base.platform.NetworkMonitor
import com.kuikly.init.common.base.platform.NetworkType

class AndroidNetworkMonitor(private val app: Application) : NetworkMonitor {
    override fun isConnected(): Boolean {
        val cm = app.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    override fun getNetworkType(): NetworkType {
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
