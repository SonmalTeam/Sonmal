package com.d202.sonmal.ui.voice

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.d202.sonmal.R
import com.d202.sonmal.adapter.VoiceAdapter
import com.d202.sonmal.databinding.FragmentVoiceBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.util.*
import kotlin.math.log


class VoiceFragment : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var binding : FragmentVoiceBinding
    private val recordingDialogFragment by lazy { RecordingDialogFragment() }
    private val resultList = mutableListOf<String>()
    private lateinit var tts : TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoiceBinding.inflate(layoutInflater, container, false)

        tts = TextToSpeech(requireContext(), this)

        binding.apply {
            rvResult.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = VoiceAdapter(itemList = resultList)
            }

            ivRecord.setOnClickListener {
                if(!recordingDialogFragment.isAdded) {
                    recordingDialogFragment.setInterface(object : RecordingDialogFragment.TranslateInterface{
                        override fun getResult(result: String) {
                            Log.d("TAG", "getResult: $result")
                            resultList.add(result)
                            rvResult.adapter!!.notifyDataSetChanged()
                            if(binding.ivMic.visibility == View.VISIBLE) {
                                val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.translate_right)
                                binding.ivMic.startAnimation(animation)
                                binding.tvIntro.startAnimation(animation)
                                binding.ivMic.visibility = View.GONE
                                binding.tvIntro.visibility = View.GONE
                            }
                        }

                    })
                    recordingDialogFragment.show(childFragmentManager, "recording")
                }
            }

            ivMacro.setOnClickListener {
                val bottomSheet = MacroBottomSheet()
                bottomSheet.show(childFragmentManager, bottomSheet.tag)
            }

            ivSpeak.setOnClickListener {
                speakOut()
            }
        }
        return binding.root
    }

    override fun onInit(p0: Int) {
        if(p0 == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN)
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