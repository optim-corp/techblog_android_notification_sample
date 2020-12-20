package jp.co.optim.techblog_android_notification_sample.application

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

class SampleApplication : Application() {

    private val lifecycleObserver = AppLifecycleObserver(this)

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }

    override fun onTerminate() {
        super.onTerminate()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
    }
}