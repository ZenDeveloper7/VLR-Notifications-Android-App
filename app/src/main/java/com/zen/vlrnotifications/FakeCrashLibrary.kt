package com.zen.vlrnotifications

/** Not a real crash reporting library!  */
class FakeCrashLibrary private constructor() {
    init {
        throw AssertionError("No instances.")
    }

    companion object {
        fun log(priority: Int, tag: String?, message: String?) {

        }

        fun logWarning(t: Throwable?) {
        }

        fun logError(t: Throwable?) {
        }
    }
}