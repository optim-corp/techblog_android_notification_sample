package jp.co.optim.techblog_android_notification_sample.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import jp.co.optim.techblog_android_notification_sample.R
import jp.co.optim.techblog_android_notification_sample.databinding.ActivityMainBinding
import jp.co.optim.techblog_android_notification_sample.extension.TAG
import jp.co.optim.techblog_android_notification_sample.extension.logI
import jp.co.optim.techblog_android_notification_sample.extension.logW
import jp.co.optim.techblog_android_notification_sample.fragment.AlertDialogFragment
import jp.co.optim.techblog_android_notification_sample.notification.NotificationPostman

/**
 * メイン画面を表示するためのアクティビティクラス.
 */
class MainActivity : AppCompatActivity(), AlertDialogFragment.Callback {

    private lateinit var binding: ActivityMainBinding

    private val notificationPostman = NotificationPostman()

    // ランタイムパーミッションの結果.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Snackbar.make(
                binding.root,
                getString(
                    if (it) R.string.granted_permission_snackbar_message
                    else R.string.denied_permission_snackbar_message
                ),
                Snackbar.LENGTH_LONG
            ).show()
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

    override fun onDismissed(tag: String?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            throw InternalError("Conflicted OS version.")
        }
        // 権限が必要な理由を提示したので、ランタイムパーミッションをリクエストする.
        logI("Launch the request of the permission.")
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    /**
     * 通知権限が許可されているか確認する.
     * https://developer.android.com/training/permissions/requesting?hl=ja
     */
    private fun checkNotificationPermission(): Boolean {
        // バージョン確認. Android13 未満は不要.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            logI("No needed the permission.")
            return true
        }
        // 権限が許可されているか確認.
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            logI("The permission is granted already.")
            return true
        }
        // 権限を必要とする理由を提示する必要があるか確認.
        // 必要がある場合は、アプリ側がその理由を明示的に説明する.
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            logI("App should show the request of permission rationale.")
            AlertDialogFragment.newInstance(
                getString(R.string.request_permission_dialog_title),
                getString(R.string.request_permission_dialog_message),
                getString(R.string.ok)
            ).show(supportFragmentManager, null)
            return false
        }
        // 権限をリクエストする.
        logI("Launch the request of the permission.")
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        return false
    }
}