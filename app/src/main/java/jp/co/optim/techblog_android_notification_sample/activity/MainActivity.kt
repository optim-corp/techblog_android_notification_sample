package jp.co.optim.techblog_android_notification_sample.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.optim.techblog_android_notification_sample.R
import jp.co.optim.techblog_android_notification_sample.extension.logI
import jp.co.optim.techblog_android_notification_sample.extension.tag
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val notificationPostman = NotificationPostman()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView_main.text = tag()
        button_showCalling.setOnClickListener {
            logI("Clicked showCalling.")
            startActivity(Intent(this, CallActivity::class.java))
        }
        button_postNotification.setOnClickListener {
            logI("Clicked postNotification.")
            notificationPostman.post(this)
        }
        button_postCallNotification.setOnClickListener {
            logI("Clicked postCallNotification.")
            notificationPostman.postCall(this)
        }
    }
}