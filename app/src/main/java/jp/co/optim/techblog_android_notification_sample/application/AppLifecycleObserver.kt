package jp.co.optim.techblog_android_notification_sample.application

import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import jp.co.optim.techblog_android_notification_sample.constants.NotificationId
import jp.co.optim.techblog_android_notification_sample.extension.logE
import jp.co.optim.techblog_android_notification_sample.extension.logI
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman

/**
 * アプリのライフサイクルを監視するためのクラス.
 */
class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {

    private val notificationPostman = NotificationPostman()

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        notificationPostman.findNotification(context, NotificationId.CALL.id)?.let {
            logI("Found call notification. Send fullScreenIntent.")
            try {
                notificationPostman.delete(context, NotificationId.CALL.id)
                it.fullScreenIntent.send()
            } catch (e : PendingIntent.CanceledException) {
                logE("Failed to send the full screen intent.")
                e.printStackTrace()
            }
        }
    }
}