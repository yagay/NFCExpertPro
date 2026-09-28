package com.yagay.YNFC

import android.content.Context

/** Thin runtime entry used by YSuite without changing standalone behavior. */
object YNFCSuiteRuntime {
    @JvmStatic
    fun get(context: Context): Any {
        val appContext = context.applicationContext
        AppLogger.attach(appContext)
        AppLogger.i("YSuite runtime attached")
        return appContext
    }
}
