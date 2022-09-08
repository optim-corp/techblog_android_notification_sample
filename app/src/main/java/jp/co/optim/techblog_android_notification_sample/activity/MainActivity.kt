package jp.co.optim.techblog_android_notification_sample.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.optim.techblog_android_notification_sample.databinding.ActivityMainBinding
import jp.co.optim.techblog_android_notification_sample.extension.TAG
import jp.co.optim.techblog_android_notification_sample.extension.logI
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val notificationPostman = NotificationPostman()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewMain.text = TAG
        binding.buttonShowCalling.setOnClickListener {
            logI("Clicked showCalling.")
            startActivity(Intent(this, CallActivity::class.java))
        }
        binding.buttonPostNotification.setOnClickListener {
            logI("Clicked postNotification.")
            notificationPostman.post(this)
        }
        binding.buttonPostCallNotification.setOnClickListener {
            logI("Clicked postCallNotification.")
            notificationPostman.postCall(this)
        }
    }
}