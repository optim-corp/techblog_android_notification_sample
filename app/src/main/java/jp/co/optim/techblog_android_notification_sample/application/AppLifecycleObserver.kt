package jp.co.optim.techblog_android_notification_sample.application

import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import jp.co.optim.techblog_android_notification_sample.constants.NotificationId
import jp.co.optim.techblog_android_notification_sample.extension.logD
import jp.co.optim.techblog_android_notification_sample.extension.logI
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman

class AppLifecycleObserver(private val context: Context) : LifecycleObserver {

    private val notificationPostman = NotificationPostman()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        logD("onCreate()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        logD("onStart()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        logD("onResume()")
        val notification = notificationPostman.findNotification(context, NotificationId.CALL.id)
        if (notification != null) {
            logI("Found call notification. Send fullScreenIntent.")
            try {
                notificationPostman.delete(context, NotificationId.CALL.id)
                notification.fullScreenIntent.send()
            } catch (e : PendingIntent.CanceledException) {
                e.printStackTrace()
            }
        }
        // 【調査２】 同じアプリで着信通知を２つ出そうとするとどうなるの？
        // 着信通知2も削除して着信画面を表示する.
        val notification2 = notificationPostman.findNotification(context, NotificationId.CALL2.id)
        if (notification2 != null) {
            logI("Found call notification2. Send fullScreenIntent.")
            try {
                notificationPostman.delete(context, NotificationId.CALL2.id)
                notification2.fullScreenIntent.send()
            } catch (e : PendingIntent.CanceledException) {
                e.printStackTrace()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        logD("onPause()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        logD("onStop()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        logD("onDestroy()")
    }
}