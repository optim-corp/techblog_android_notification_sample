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

class CallActivity : AppCompatActivity(), CallingFragment.Callback, TalkingFragment.Callback {

    companion object {
        const val CALL_ACCEPTED = "call_accepted"
    }

    private val notificationPostman = NotificationPostman()

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        notificationPostman.delete(this, NotificationId.CALL.id)

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