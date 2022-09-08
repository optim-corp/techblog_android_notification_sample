package jp.co.optim.techblog_android_notification_sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.co.optim.techblog_android_notification_sample.databinding.FragmentCallingBinding
import jp.co.optim.techblog_android_notification_sample.extension.TAG

class CallingFragment : Fragment() {

    interface Callback {
        fun onCalledAccept()
        fun onCalledRefuse()
    }

    private var _binding: FragmentCallingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = run {
        _binding = FragmentCallingBinding.inflate(inflater, container, false)

        binding.textViewCalling.text = TAG
        binding.buttonAccept.setOnClickListener {
            getCallback()?.onCalledAccept()
        }
        binding.buttonRefuse.setOnClickListener {
            getCallback()?.onCalledRefuse()
        }

        binding.root
    }

    private fun getCallback(): Callback? {
        val callbackActivity = activity
        return if (callbackActivity is Callback) callbackActivity else null
    }
}