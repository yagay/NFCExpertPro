package com.yagay.YNFC.system

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Toast
import java.util.concurrent.TimeUnit

class RootShell(context: Context) {
    companion object {
        private const val ROOT_OK_CACHE_MS = 60_000L
        private const val ROOT_FAILURE_CACHE_MS = 5_000L
        private const val SUITE_ROOT_GATEWAY = "com.yagay.suite.core.SuiteRootGateway"
        private const val PLUGIN_ID = "ynfc"
    }

    private val appContext = context.applicationContext
    @Volatile private var rootAvailableCache: Boolean? = null
    @Volatile private var rootCacheCheckedAt: Long = 0L
    @Volatile private var lastRootToastAt: Long = 0L

    fun run(command: String, timeoutSeconds: Long = 20, maxChars: Int = 1_000_000, showToast: Boolean = true): String {
        val host = hostGateway()
        if (host != null) {
            val result = hostRun(host, command, timeoutSeconds)
            if (!result.success && showToast && result.code == -1) notifyRootUnavailable()
            val output = buildString {
                append(result.stdout.take(maxChars))
                if (result.stderr.isNotBlank() && length < maxChars) {
                    if (isNotEmpty() && last() != '\n') append('\n')
                    append(result.stderr.take(maxChars - length))
                }
                if (length < maxChars) {
                    if (isNotEmpty() && last() != '\n') append('\n')
                    appendLine(if (result.timedOut) "[timeout=${timeoutSeconds}s]" else "[exit=${result.code}]")
                }
            }
            return output
        }

        if (!ensureRootAccess(showToast)) return "ROOT_UNAVAILABLE"
        return try {
            val process = ProcessBuilder("su", "-c", command).redirectErrorStream(true).start()
            val output = StringBuffer()
            val reader = Thread({
                runCatching {
                    process.inputStream.bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            if (output.length < maxChars) {
                                val remaining = maxChars - output.length
                                val piece = if (line.length + 1 <= remaining) line + "\n" else line.take(remaining)
                                output.append(piece)
                            }
                        }
                    }
                }
            }, "YNFC-RootReader").apply { isDaemon = true; start() }

            val finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                process.waitFor(2, TimeUnit.SECONDS)
            }
            reader.join(1500)
            if (!finished) output.appendLine("[timeout=${timeoutSeconds}s]")
            else output.appendLine("[exit=${process.exitValue()}]")
            output.toString()
        } catch (t: Throwable) {
            invalidateRootCache()
            if (showToast) notifyRootUnavailable()
            "ERROR ${t.javaClass.simpleName}: ${t.message}"
        }
    }

    fun invalidateRootCache() {
        rootAvailableCache = null
        rootCacheCheckedAt = 0L
    }

    @Synchronized
    private fun ensureRootAccess(showToast: Boolean): Boolean {
        val now = SystemClock.elapsedRealtime()
        rootAvailableCache?.let { cached ->
            val ttl = if (cached) ROOT_OK_CACHE_MS else ROOT_FAILURE_CACHE_MS
            if (now - rootCacheCheckedAt in 0 until ttl) {
                if (!cached && showToast) notifyRootUnavailable()
                return cached
            }
        }

        val host = hostGateway()
        val ok = if (host != null) {
            val result = hostRun(host, "id -u", 4)
            result.success && result.stdout.lineSequence().any { it.trim() == "0" }
        } else {
            try {
                val process = ProcessBuilder("su", "-c", "id -u").redirectErrorStream(true).start()
                val finished = process.waitFor(4, TimeUnit.SECONDS)
                val output = if (finished) process.inputStream.bufferedReader().readText().trim() else ""
                if (!finished) process.destroyForcibly()
                finished && process.exitValue() == 0 && output.lineSequence().any { it.trim() == "0" }
            } catch (_: Throwable) {
                false
            }
        }
        rootAvailableCache = ok
        rootCacheCheckedAt = now
        if (!ok && showToast) notifyRootUnavailable()
        return ok
    }

    private fun hostGateway(): Class<*>? = runCatching {
        Class.forName(SUITE_ROOT_GATEWAY, false, javaClass.classLoader)
    }.getOrNull()

    private fun hostRun(gateway: Class<*>, command: String, timeoutSeconds: Long): HostRootResult =
        runCatching {
            val method = gateway.getMethod(
                "executeFromPlugin",
                String::class.java,
                String::class.java,
                String::class.java,
                java.lang.Long.TYPE,
            )
            val raw = method.invoke(null, PLUGIN_ID, "root-shell", command, timeoutSeconds)
                ?: error("YSuite root gateway returned null")
            val type = raw.javaClass
            HostRootResult(
                code = (type.getMethod("getCode").invoke(raw) as Number).toInt(),
                stdout = type.getMethod("getStdout").invoke(raw) as? String ?: "",
                stderr = type.getMethod("getStderr").invoke(raw) as? String ?: "",
                timedOut = type.getMethod("getTimedOut").invoke(raw) as? Boolean ?: false,
                success = type.getMethod("getSuccess").invoke(raw) as? Boolean ?: false,
            )
        }.getOrElse { error ->
            HostRootResult(
                code = -1,
                stdout = "",
                stderr = "YSuite root gateway error: ${error.javaClass.simpleName}: ${error.message}",
                timedOut = false,
                success = false,
            )
        }

    private data class HostRootResult(
        val code: Int,
        val stdout: String,
        val stderr: String,
        val timedOut: Boolean,
        val success: Boolean,
    )

    private fun notifyRootUnavailable() {
        val now = SystemClock.elapsedRealtime()
        if (now - lastRootToastAt < 3000L) return
        lastRootToastAt = now
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(appContext, "Root 获取失败，请在 Root 管理器中授予本应用权限", Toast.LENGTH_LONG).show()
        }
    }
}
