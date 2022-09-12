package jp.co.optim.techblog_android_notification_sample.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

/**
 * アラートダイアログを表示するためのクラス.
 */
class AlertDialogFragment : DialogFragment() {

    interface Callback {
        fun onDismissed(tag: String?)
    }

    companion object {
        private const val ARGS_TITLE = "title"
        private const val ARGS_MESSAGE = "message"
        private const val ARGS_BTN_TEXT = "btn_text"

        fun newInstance(
            title: String? = null,
            message: String? = null,
            btnText: String? = null,
            cancelable: Boolean = false
        ): DialogFragment = AlertDialogFragment().apply {
            arguments = bundleOf(
                Pair(ARGS_TITLE, title),
                Pair(ARGS_MESSAGE, message),
                Pair(ARGS_BTN_TEXT, btnText),
            )
            isCancelable = cancelable
        }
    }

    private val args: Bundle by lazy {
        arguments ?: throw IllegalArgumentException("Not found arguments.")
    }
    private val title: String? by lazy { args.getString(ARGS_TITLE) }
    private val message: String? by lazy { args.getString(ARGS_MESSAGE) }
    private val btnText: String? by lazy { args.getString(ARGS_BTN_TEXT) }
    private val callback: Callback? by lazy { requireActivity() as? Callback }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireActivity()).apply {
            title?.let { setTitle(it) }
            message?.let { setMessage(it) }
            btnText?.let {
                setPositiveButton(it) { _, _ ->
                    dismiss()
                    callback?.onDismissed(tag)
                }
            }
        }.create()

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        callback?.onDismissed(tag)
    }
}