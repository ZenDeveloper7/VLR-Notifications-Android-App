package com.zen.vlrnotifications

import android.app.Application
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant
import timber.log.Timber.Tree
import kotlin.math.min


class VLRApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            plant(DevelopmentTree())
        } else {
            plant(ReleaseTree())
        }
    }
}

class DevelopmentTree : DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return String.format(
            "%s %s | %s",
            element.lineNumber,
            super.createStackElementTag(element),
            element.methodName
        )
    }
}

class ReleaseTree : Tree() {
    override fun isLoggable(tag: String?, priority: Int): Boolean {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return false
        }
        return true
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (isLoggable(priority)) {
            if (message.length < MAX_LOG_LENGTH) {
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, message)
                } else {
                    Log.println(priority, tag, message)
                }
                return
            }

            var i = 0
            val length = message.length
            while (i < length) {
                var newline = message.indexOf('\n', i)
                newline = if (newline != -1) newline else length
                do {
                    val end = min(newline.toDouble(), (i + MAX_LOG_LENGTH).toDouble())
                        .toInt()
                    val part = message.substring(i, end)
                    if (priority == Log.ASSERT) {
                        Log.wtf(tag, part)
                    } else {
                        Log.println(priority, tag, part)
                    }
                    i = end
                } while (i < newline)
                i++
            }
        }
    }

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }
}
