package com.kuikly.init.common.base.platform

import android.app.Activity
import android.app.Application
import android.os.Bundle

actual object AppContext {
    actual var androidContext: Any? = null

    /** 当前活跃的 Activity（通过 ActivityLifecycleCallbacks 自动追踪） */
    var currentActivity: Activity? = null
        private set

    fun init(application: Application) {
        androidContext = application
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                if (currentActivity === activity) {
                    currentActivity = null
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    val application: Application
        get() = androidContext as? Application
            ?: throw IllegalStateException("AppContext not initialized. Call AppContext.init() in Application.onCreate()")
}
