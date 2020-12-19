package jp.co.optim.techblog_android_notification_sample.extension

import android.util.Log

fun Any.tag(): String = this::class.java.simpleName

fun Any.logV(message: String) = Log.v(tag(), message)

fun Any.logD(message: String) = Log.d(tag(), message)

fun Any.logI(message: String) = Log.i(tag(), message)

fun Any.logW(message: String) = Log.w(tag(), message)

fun Any.logE(message: String) = Log.e(tag(), message)
