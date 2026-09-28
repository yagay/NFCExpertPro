package com.yagay.YNFC

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLogger {
    private val lines = ArrayDeque<String>()
    private val format = SimpleDateFormat("HH:mm:ss.SSS", Locale.ROOT)
    @Volatile private var appContext: Context? = null

    fun attach(context: Context) {
        appContext = context.applicationContext
    }

    @Synchronized
    fun i(message: String) {
        val line = "[${format.format(Date())}] APP: $message"
        lines.addLast(line)
        while (lines.size > 1000) lines.removeFirst()
        android.util.Log.i("YNFC", message)
        mirrorToYSuite("I", message, null)
    }

    @Synchronized
    fun e(message: String, error: Throwable? = null) {
        val detail = if (error == null) message else "$message ${error.javaClass.simpleName}: ${error.message}"
        val line = "[${format.format(Date())}] APP: $detail"
        lines.addLast(line)
        while (lines.size > 1000) lines.removeFirst()
        android.util.Log.e("YNFC", message, error)
        mirrorToYSuite("E", message, error)
    }

    private fun mirrorToYSuite(level: String, message: String, error: Throwable?) {
        val context = appContext ?: return
        runCatching {
            val clazz = Class.forName("com.yagay.suite.core.SuiteLog")
            val instance = clazz.getField("INSTANCE").get(null)
            if (level == "E") {
                clazz.getMethod(
                    "e",
                    Context::class.java,
                    String::class.java,
                    String::class.java,
                    Throwable::class.java
                ).invoke(instance, context, "ynfc", message, error)
            } else {
                clazz.getMethod(
                    "i",
                    Context::class.java,
                    String::class.java,
                    String::class.java
                ).invoke(instance, context, "ynfc", message)
            }
        }
    }

    @Synchronized
    fun readAll(): String = lines.joinToString("\n")

    @Synchronized
    fun clear() {
        lines.clear()
    }
}
