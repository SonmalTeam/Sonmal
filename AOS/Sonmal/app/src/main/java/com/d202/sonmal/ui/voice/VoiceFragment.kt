package com.d202.sonmal.ui.voice

import android.Manifest
import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.ViewBindingAdapter.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.d202.sonmal.R
import com.d202.sonmal.adapter.VoiceAdapter
import com.d202.sonmal.databinding.FragmentVoiceBinding
import com.d202.sonmal.databinding.ToastLayoutBinding
import com.d202.sonmal.ui.call.viewmodel.CallViewModel
import com.d202.sonmal.utils.MainSharedPreference
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.mediapipe.solutioncore.CameraInput
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.util.*


class VoiceFragment : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var binding : FragmentVoiceBinding
    private val viewModel: CallViewModel by viewModels()
    private val recordingDialogFragment by lazy { RecordingDialogFragment() }
    private val resultList = mutableListOf<String>()
    private lateinit var tts : TextToSpeech
    private lateinit var voiceAdapter: VoiceAdapter

    private val REQUIRED_PERMISSIONS = mutableListOf(
        Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO).toTypedArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoiceBinding.inflate(layoutInflater, container, false)

        voiceAdapter = VoiceAdapter()

        tts = TextToSpeech(requireContext(), this)

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(requireContext(), "권한을 다시 설정해주세요!", Toast.LENGTH_SHORT).show()
            }

        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setPermissions(*REQUIRED_PERMISSIONS)
            .check()


        binding.apply {
            rvResult.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = voiceAdapter
            }

            ivRecord.setOnClickListener {
                viewModel.startSTT(requireContext(), MainSharedPreference(requireContext()).token.toString())
//                if(!recordingDialogFragment.isAdded) {
//                    recordingDialogFragment.setInterface(object : RecordingDialogFragment.TranslateInterface{
//                        override fun getResult(result: String) {
//                            resultList.add(result)
//                            rvResult.adapter!!.notifyDataSetChanged()
//                            if(binding.ivMic.visibility == View.VISIBLE) {
//                                val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.translate_right)
//                                binding.ivMic.startAnimation(animation)
//                                binding.tvIntro.startAnimation(animation)
//                                binding.ivMic.visibility = View.GONE
//                                binding.tvIntro.visibility = View.GONE
//                            }
//                        }
//
//                    })
//                    recordingDialogFragment.show(childFragmentManager, "recording")
//                }
            }

            ivMacro.setOnClickListener {
                val bottomSheet = MacroBottomSheet()
                bottomSheet.show(childFragmentManager, bottomSheet.tag)
            }

            ltSpeak.setOnClickListener {
                speakOut()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            sttResult.observe(viewLifecycleOwner){
                if (it.isNotBlank()) {
                    resultList.add(it)
                    voiceAdapter.itemList = resultList
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startSTT(requireContext(), MainSharedPreference(requireContext()).token.toString())
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopSTT()

    }

    override fun onInit(p0: Int) {
        if(p0 == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN)
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(p0: String?) {
                    (activity as Activity).runOnUiThread {
                        binding.ltSpeak.playAnimation()
                    }
                }

                override fun onDone(p0: String?) {
                    (activity as Activity).runOnUiThread {
                        binding.ltSpeak.pauseAnimation()
                        binding.ltSpeak.progress = 0f
                    }
                }

                override fun onError(p0: String?) {
                }

            })
        }
    }

    private fun speakOut() {
        val text = binding.etContent.text as CharSequence
        tts.setPitch(1f)
        tts.setSpeechRate(1f)
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "id1")
        binding.etContent.setText("")
    }
}