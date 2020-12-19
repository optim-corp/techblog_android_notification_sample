package jp.co.optim.techblog_android_notification_sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.co.optim.techblog_android_notification_sample.R
import jp.co.optim.techblog_android_notification_sample.extension.tag
import kotlinx.android.synthetic.main.fragment_calling.*

class CallingFragment : Fragment() {

    interface Callback {
        fun onCalledAccept()
        fun onCalledRefuse()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_calling, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView_calling.text = tag()
        button_accept.setOnClickListener {
            getCallback()?.onCalledAccept()
        }
        button_refuse.setOnClickListener {
            getCallback()?.onCalledRefuse()
        }
    }

    private fun getCallback(): Callback? {
        val callbackActivity = activity
        return if (callbackActivity is Callback) callbackActivity else null
    }
}