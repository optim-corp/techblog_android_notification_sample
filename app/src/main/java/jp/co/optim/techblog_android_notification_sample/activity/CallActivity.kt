package jp.co.optim.techblog_android_notification_sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import jp.co.optim.techblog_android_notification_sample.R
import jp.co.optim.techblog_android_notification_sample.constants.NotificationId
import jp.co.optim.techblog_android_notification_sample.extension.logD
import jp.co.optim.techblog_android_notification_sample.extension.logI
import jp.co.optim.techblog_android_notification_sample.extension.tag
import jp.co.optim.techblog_android_notification_sample.fragment.CallingFragment
import jp.co.optim.techblog_android_notification_sample.fragment.TalkingFragment
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman
import jp.co.optim.techblog_android_notification_sample.receiver.CallRefusedReceiver

class CallActivity : AppCompatActivity(), CallingFragment.Callback, TalkingFragment.Callback {

    companion object {
        const val CALL_ACCEPTED = "call_accepted"
        const val NOTIFICATION_ID = "notification_id"
    }

    private val notificationPostman = NotificationPostman()

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        logD("onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        // 【同じアプリで着信通知を２つ出そうとするとどうなるの？】
        // 着信領域から着信応答もしくは通知タップした着信通知のみを削除.
        val notificationId = when (intent.getIntExtra(CallRefusedReceiver.NOTIFICATION_ID, 0)) {
            NotificationId.CALL.id -> NotificationId.CALL
            NotificationId.CALL_ACCEPT.id -> NotificationId.CALL
            NotificationId.CALL2.id -> NotificationId.CALL2
            NotificationId.CALL_ACCEPT2.id -> NotificationId.CALL2
            else -> return
        }
        notificationPostman.delete(this, notificationId.id)

        val callAccepted = intent.getBooleanExtra(CALL_ACCEPTED, false)
        logI("Is call accepted: $callAccepted")
        replaceFragment(if (callAccepted) TalkingFragment() else CallingFragment())
    }

    override fun onCalledAccept() {
        logD("onCalledAccept()")
        replaceFragment(TalkingFragment())
    }

    override fun onCalledRefuse() {
        logD("onCalledRefuse()")
        finish()
    }

    override fun onCalledEnd() {
        logD("onCalledEnd()")
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        logI("Replace fragment: ${fragment.tag()}")
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }
}