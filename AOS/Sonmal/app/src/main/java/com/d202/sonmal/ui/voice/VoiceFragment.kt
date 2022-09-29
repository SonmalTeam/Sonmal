package com.d202.sonmal.ui.voice

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.d202.sonmal.R
import com.d202.sonmal.adapter.VoiceAdapter
import com.d202.sonmal.databinding.FragmentVoiceBinding
import java.util.*


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
//                    findNavController().navigate(VoiceFragmentDirections.actionVoiceFragmentToMacroBottomSheet())
                    //recordingDialogFragment.show(childFragmentManager, "recording")
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