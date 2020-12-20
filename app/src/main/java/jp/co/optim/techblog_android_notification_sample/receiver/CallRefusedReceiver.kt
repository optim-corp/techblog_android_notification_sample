package jp.co.optim.techblog_android_notification_sample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import jp.co.optim.techblog_android_notification_sample.constants.NotificationId
import jp.co.optim.techblog_android_notification_sample.extension.logD
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman

class CallRefusedReceiver: BroadcastReceiver() {

    private val notificationPostman = NotificationPostman();

    override fun onReceive(context: Context?, intent: Intent?) {
        logD("onReceive()")

        if (context != null) {
            // 通知領域から着信通知を削除
            notificationPostman.delete(context, NotificationId.CALL.id)
        }
    }
}