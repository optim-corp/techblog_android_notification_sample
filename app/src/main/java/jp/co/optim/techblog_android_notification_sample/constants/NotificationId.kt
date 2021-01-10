package jp.co.optim.techblog_android_notification_sample.constants

import java.util.*

/**
 * 通知に指定するID
 * 正の数をランダム使用するため、負の数を固定値とすることで重複を防ぐ.
 */
enum class NotificationId(val id: Int) {

    /**
     * 着信
     */
    CALL(-1000),

    /**
     * 着信応答
     */
    CALL_ACCEPT(-1001),

    /**
     * 着信拒否
     */
    CALL_REFUSE(-1010),

    /**
     * 【調査２】 同じアプリで着信通知を２つ出そうとするとどうなるの？
     * ２つ目の着信通知のための通知IDを定義する.
     */
    /**
     * 着信
     */
    CALL2(-2000),

    /**
     * 着信応答
     */
    CALL_ACCEPT2(-2001),

    /**
     * 着信拒否
     */
    CALL_REFUSE2(-2010),
    ;

    companion object {
        // ランダムな正の数のIDを生成する.
        fun generateRandomId(): Int = Random().nextInt()
    }
}