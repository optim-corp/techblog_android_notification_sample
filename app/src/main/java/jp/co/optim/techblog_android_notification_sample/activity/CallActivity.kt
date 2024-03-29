package jp.co.optim.techblog_android_notification_sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import jp.co.optim.techblog_android_notification_sample.R
import jp.co.optim.techblog_android_notification_sample.constants.NotificationId
import jp.co.optim.techblog_android_notification_sample.databinding.ActivityCallBinding
import jp.co.optim.techblog_android_notification_sample.extension.TAG
import jp.co.optim.techblog_android_notification_sample.extension.logD
import jp.co.optim.techblog_android_notification_sample.extension.logI
import jp.co.optim.techblog_android_notification_sample.fragment.CallingFragment
import jp.co.optim.techblog_android_notification_sample.fragment.TalkingFragment
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman

/**
 * ダミーの着信画面・通話画面を表示するためのアクティビティクラス.
 * @see CallingFragment 着信画面
 * @see TalkingFragment 通話画面
 */
class CallActivity : AppCompatActivity(), CallingFragment.Callback, TalkingFragment.Callback {

    companion object {
        const val CALL_ACCEPTED = "call_accepted"
    }

    private lateinit var binding: ActivityCallBinding

    private val notificationPostman = NotificationPostman()

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        logD("onCreate()")
        super.onCreate(savedInstanceState)

        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 通知領域から着信通知を削除
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
        logI("Replace fragment: ${fragment.TAG}")
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }
}