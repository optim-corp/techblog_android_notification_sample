package jp.co.optim.techblog_android_notification_sample.constants

/**
 * 通知のチャンネルタイプ
 */
enum class ChannelType(val id: String) {

    /**
     * 通常の通知
     */
    NORMAL("normal"),

    /**
     * 着信通知
     */
    CALL("call"),
}