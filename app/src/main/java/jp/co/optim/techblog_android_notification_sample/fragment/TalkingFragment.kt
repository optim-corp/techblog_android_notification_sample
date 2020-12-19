package jp.co.optim.techblog_android_notification_sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.co.optim.techblog_android_notification_sample.R
import jp.co.optim.techblog_android_notification_sample.extension.tag
import kotlinx.android.synthetic.main.fragment_talking.*

class TalkingFragment : Fragment() {

    interface Callback {
        fun onCalledEnd()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_talking, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView_talking.text = tag()
        button_end.setOnClickListener {
            getCallback()?.onCalledEnd()
        }
    }

    private fun getCallback(): Callback? {
        val callbackActivity = activity
        return if (callbackActivity is Callback) callbackActivity else null
    }
}