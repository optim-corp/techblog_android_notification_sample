package jp.co.optim.techblog_android_notification_sample.constants

import kotlin.random.Random

enum class NotificationId(val id: Int) {

    CALL(-1000),

    CALL_ACCEPT(-1001),

    CALL_REFUSE(-1010),
    ;

    companion object {
        fun generateRandomId(): Int = Random.nextInt()
    }
}