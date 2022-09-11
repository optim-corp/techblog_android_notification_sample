package jp.co.optim.techblog_android_notification_sample.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import jp.co.optim.techblog_android_notification_sample.databinding.ActivityMainBinding
import jp.co.optim.techblog_android_notification_sample.extension.TAG
import jp.co.optim.techblog_android_notification_sample.extension.logI
import jp.co.optim.techblog_android_notification_sample.extension.logW
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val notificationPostman = NotificationPostman()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                logI("The permission is granted just now.")
            } else {
                logW("The permission is denied.")
            }
        }

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
            if (checkNotificationPermission()) {
                notificationPostman.post(this)
            }
        }
        binding.buttonPostCallNotification.setOnClickListener {
            logI("Clicked postCallNotification.")
            if (checkNotificationPermission()) {
                notificationPostman.postCall(this)
            }
        }
        binding.buttonPostNewCallNotification.let { btn ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                btn.setOnClickListener {
                    logI("Clicked postNewCallNotification.")
                    if (checkNotificationPermission()) {
                        notificationPostman.postNewCall(this)
                    }
                }
            } else {
                logW("Cannot use postNewCallNotification. OS version is less than API31.")
                btn.isEnabled = false
            }
        }
    }

    private fun checkNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            logI("No needed the permission.")
            return true
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            logI("The permission is granted already.")
            return true
        }
        logI("The permission is not granted yet.")
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.POST_NOTIFICATIONS)) {
            logI("App should show the request of permission rationale.")
            // TODO: Must be implemented here!
            return false
        }
        logI("Launch the request of the permission.")
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        return false
    }
}