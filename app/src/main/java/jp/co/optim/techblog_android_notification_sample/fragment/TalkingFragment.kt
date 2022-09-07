package jp.co.optim.techblog_android_notification_sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.co.optim.techblog_android_notification_sample.databinding.FragmentTalkingBinding
import jp.co.optim.techblog_android_notification_sample.extension.tag

class TalkingFragment : Fragment() {

    interface Callback {
        fun onCalledEnd()
    }

    private var _binding: FragmentTalkingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = run {
        _binding = FragmentTalkingBinding.inflate(inflater, container, false)

        binding.textViewTalking.text = tag()
        binding.buttonEnd.setOnClickListener {
            getCallback()?.onCalledEnd()
        }

        binding.root
    }

    private fun getCallback(): Callback? {
        val callbackActivity = activity
        return if (callbackActivity is Callback) callbackActivity else null
    }
}