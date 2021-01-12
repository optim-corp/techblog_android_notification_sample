package jp.co.optim.techblog_android_notification_sample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import jp.co.optim.techblog_android_notification_sample.constants.NotificationId
import jp.co.optim.techblog_android_notification_sample.extension.logD
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman

class CallRefusedReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "notification_id"
    }

    private val notificationPostman = NotificationPostman();

    override fun onReceive(context: Context?, intent: Intent?) {
        logD("onReceive()")

        if (context != null && intent != null) {
            // 【同じアプリで着信通知を２つ出そうとするとどうなるの？】
            // 着信領域から着信拒否した着信通知のみを削除.
            val notificationId = when (intent.getIntExtra(NOTIFICATION_ID, 0)) {
                NotificationId.CALL_REFUSE.id -> NotificationId.CALL
                NotificationId.CALL_REFUSE2.id -> NotificationId.CALL2
                else -> return
            }
            notificationPostman.delete(context, notificationId.id)
        }
    }
}