package com.kuikly.init.common.base.platform

import android.app.Application

actual object AppContext {
    actual var androidContext: Any? = null

    fun init(application: Application) {
        androidContext = application
    }

    val application: Application
        get() = androidContext as? Application
            ?: throw IllegalStateException("AppContext not initialized. Call AppContext.init() in Application.onCreate()")
}
