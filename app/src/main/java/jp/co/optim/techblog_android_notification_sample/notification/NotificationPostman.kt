package jp.co.optim.techblog_android_notification_sample.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import jp.co.optim.techblog_android_notification_sample.R
import jp.co.optim.techblog_android_notification_sample.activity.CallActivity
import jp.co.optim.techblog_android_notification_sample.constants.ChannelType
import jp.co.optim.techblog_android_notification_sample.constants.NotificationId
import jp.co.optim.techblog_android_notification_sample.extension.logD
import jp.co.optim.techblog_android_notification_sample.receiver.CallRefusedReceiver
import java.util.*

class NotificationPostman {

    fun post(
        context: Context,
        @StringRes titleResId: Int = R.string.app_name,
        @StringRes messageResId: Int = R.string.notification_message
    ) {
        logD("post()")
        val title = context.getString(titleResId)
        val message = context.getString(messageResId)
        val randomId = NotificationId.generateRandomId()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ChannelType.NORMAL.id, title, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, ChannelType.NORMAL.id).apply {
            setSmallIcon(R.drawable.app_icon_small)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.app_icon))
            setContentTitle(title)
            setContentText(message)
            setContentIntent(defaultPendingIntent(context, randomId))
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setAutoCancel(true)
            setStyle(NotificationCompat.BigTextStyle().bigText(message))
        }.build()

        manager.notify(randomId, notification)
    }

    fun postCall(
        context: Context,
        @StringRes titleResId: Int = R.string.app_name,
        @StringRes messageResId: Int = R.string.notification_call_message
    ) {
        logD("postCall()")
        val title = context.getString(titleResId)
        val message = context.getString(messageResId)
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//        val ringtoneUri = Uri.parse(String.format(
//            Locale.US, "%s://%s/%d",
//            ContentResolver.SCHEME_ANDROID_RESOURCE, context.packageName, R.raw.custom_ringtone
//        ))

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ChannelType.CALL.id, title, NotificationManager.IMPORTANCE_HIGH).apply {
                val attributes = AudioAttributes.Builder().apply {
                    setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                }.build()
                setSound(ringtoneUri, attributes)
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, ChannelType.CALL.id).apply {
            setSmallIcon(R.drawable.app_icon_small)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.app_icon))
            setContentTitle(title)
            setContentText(message)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setDefaults(NotificationCompat.DEFAULT_SOUND)
            setCategory(NotificationCompat.CATEGORY_CALL)
            setAutoCancel(true)
            setStyle(NotificationCompat.BigTextStyle().bigText(message))
            setSound(ringtoneUri, AudioManager.STREAM_RING)
            setFullScreenIntent(callPendingIntent(context, NotificationId.CALL.id), true)
            addAction(
                R.drawable.refuse_button,
                getColorString(context, R.string.button_refuse, R.color.colorRefuse),
                refusePendingIntent(context, NotificationId.CALL_REFUSE.id)
            )
            addAction(
                R.drawable.accept_button,
                getColorString(context, R.string.button_accept, R.color.colorAccept),
                callPendingIntent(context, NotificationId.CALL_ACCEPT.id, true)
            )
        }.build()

        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_INSISTENT
        manager.notify(NotificationId.CALL.id, notification)
    }

    fun delete(context: Context, notificationId: Int) {
        logD("delete($notificationId)")
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    fun findNotification(context: Context, notificationId: Int): Notification? {
        logD("findNotification($notificationId)")
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = manager.activeNotifications
        for (notification in notifications) {
            if (notification.id == notificationId) return notification.notification
        }
        return null
    }

    private fun defaultPendingIntent(
        context: Context, notificationId: Int
    ): PendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        context.packageManager.getLaunchIntentForPackage(context.packageName),
        PendingIntent.FLAG_CANCEL_CURRENT
    )

    private fun callPendingIntent(
        context: Context, notificationId: Int, accepted: Boolean = false
    ): PendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        Intent(context, CallActivity::class.java).putExtra(CallActivity.CALL_ACCEPTED, accepted),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun refusePendingIntent(
        context: Context, notificationId: Int
    ): PendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        Intent(context, CallRefusedReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getColorString(
        context: Context, @StringRes stringResId: Int, @ColorRes colorResId: Int
    ): CharSequence = HtmlCompat.fromHtml(
        String.format(
            Locale.US, "<font color=\"%d\">%s</font>",
            ContextCompat.getColor(context, colorResId),
            context.getString(stringResId)
        ),
        HtmlCompat.FROM_HTML_MODE_LEGACY
    )
}