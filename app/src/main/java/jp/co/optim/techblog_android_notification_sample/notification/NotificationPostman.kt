package jp.co.optim.techblog_android_notification_sample.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
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

/**
 * 通知関連の処理をするためのクラス.
 */
class NotificationPostman {

    /**
     * 通常の通知をポストする.
     */
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

        // Android O 以降はチャンネルを実装する必要がある.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ChannelType.NORMAL.id, title, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, ChannelType.NORMAL.id).apply {  // どのチャンネルかを紐づける.
            setSmallIcon(R.drawable.app_icon_small)  // ステータスバーで表示されるアプリアイコン
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.app_icon))  // 通知領域内の通知で表示されるアイコン
            setContentTitle(title)  // 通知タイトル
            setContentText(message)  // 通知メッセージ
            setContentIntent(defaultPendingIntent(context, randomId))  // 通知タップ時の挙動. 例の場合はアプリそのものが起動する.
            setPriority(NotificationCompat.PRIORITY_HIGH)  // プライオリティ (Android O 未満対応)
            setDefaults(NotificationCompat.DEFAULT_ALL)  // システムデフォルト (Android O 未満対応)
            setAutoCancel(true)  // タップしたら自動キャンセルする
            setStyle(NotificationCompat.BigTextStyle().bigText(message))  // スタイル（通知領域内の通知で長い文字を表示する.）
        }.build()

        manager.notify(randomId, notification)
    }

    /**
     * 着信通知をポストする.
     */
    fun postCall(
        context: Context,
        @StringRes titleResId: Int = R.string.app_name,
        @StringRes messageResId: Int = R.string.notification_call_message
    ) {
        logD("postCall()")
        val title = context.getString(titleResId)
        val message = context.getString(messageResId)

        // OSに設定された着信音
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        // アプリで専用の着信音を出したい場合は、res/raw 配下に MP3 ファイルを設置して以下で可能.
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
                setSound(ringtoneUri, attributes)  // 1. チャンネルに着信音を設定
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, ChannelType.CALL.id).apply {
            setSmallIcon(R.drawable.app_icon_small)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.app_icon))
            setContentTitle(title)
            setContentText(message)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setDefaults(NotificationCompat.DEFAULT_SOUND)  // 1. システムデフォルトにサウンドを指定 (Android O 未満対応)
            setCategory(NotificationCompat.CATEGORY_CALL)  // 2. カテゴリにCALLを設定
            setAutoCancel(true)
            setStyle(NotificationCompat.BigTextStyle().bigText(message))
            setSound(ringtoneUri, AudioManager.STREAM_RING)  // 1. 通知に着信音を設定 (Android O 未満対応)
            setFullScreenIntent(callPendingIntent(context, NotificationId.CALL.id), true)  // 3. ContentIntent の代わりに FullScreenIntent を設定
            addAction(  // 4. 「拒否」ボタンを追加
                R.drawable.refuse_button,
                getColorString(context, R.string.button_refuse, R.color.colorRefuse),
                refusePendingIntent(context, NotificationId.CALL_REFUSE.id)
            )
            addAction(  // 4. 「応答」ボタンを追加
                R.drawable.accept_button,
                getColorString(context, R.string.button_accept, R.color.colorAccept),
                callPendingIntent(context, NotificationId.CALL_ACCEPT.id, true)
            )
        }.build()

        // 5. Notificationにフラグを追加
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_INSISTENT
        manager.notify(NotificationId.CALL.id, notification)
    }

    /**
     * 新しい着信通知をポストする.
     * Android 12 (API 31) 以降の OS バージョンで使用可能.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun postNewCall(
        context: Context,
        @StringRes titleResId: Int = R.string.app_name,
        callerName: String = "OPTiM Corp."
    ) {
        logD("postNewCall()")
        val title = context.getString(titleResId)
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val channel = NotificationChannel(ChannelType.CALL.id, title, NotificationManager.IMPORTANCE_HIGH).apply {
            val attributes = AudioAttributes.Builder().apply {
                setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            }.build()
            setSound(ringtoneUri, attributes)
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        // 発信者の情報を生成
        val person = Person.Builder().apply {
            setName(callerName) // 発信者名
            setImportant(true)  // 通知の優先度を上げるため重要人物であることを示すフラグを設定
        }.build()

        val notification = Notification.Builder(context, ChannelType.CALL.id).apply {
            setSmallIcon(R.drawable.app_icon_small)
            setFullScreenIntent(callPendingIntent(context, NotificationId.CALL.id), true)
            // 通知スタイルを着信専用に設定
            setStyle(
                Notification.CallStyle.forIncomingCall(
                    person,
                    refusePendingIntent(context, NotificationId.CALL_REFUSE.id),
                    callPendingIntent(context, NotificationId.CALL_ACCEPT.id, true)
                )
            )
        }.build()

        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_INSISTENT
        manager.notify(NotificationId.CALL.id, notification)
    }

    /**
     * 該当のIDに紐づく通知を削除する.
     */
    fun delete(context: Context, notificationId: Int) {
        logD("delete($notificationId)")
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    /**
     * 該当のIDに紐づく通知が通知領域内に存在していれば取得する.
     */
    fun findNotification(context: Context, notificationId: Int): Notification? {
        logD("findNotification($notificationId)")
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = manager.activeNotifications  // API 23 以降に実装されたメソッド
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
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
    )

    private fun callPendingIntent(
        context: Context, notificationId: Int, accepted: Boolean = false
    ): PendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        Intent(context, CallActivity::class.java).putExtra(CallActivity.CALL_ACCEPTED, accepted),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun refusePendingIntent(
        context: Context, notificationId: Int
    ): PendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        Intent(context, CallRefusedReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
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