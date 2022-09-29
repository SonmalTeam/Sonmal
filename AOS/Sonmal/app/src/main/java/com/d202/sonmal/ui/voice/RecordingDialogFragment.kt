package com.d202.sonmal.ui.voice

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.d202.sonmal.databinding.FragmentRecordingDialogBinding
import java.util.*
import kotlin.concurrent.thread

class RecordingDialogFragment: DialogFragment() {
    private var _binding: FragmentRecordingDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var translateInterface : TranslateInterface

    public interface TranslateInterface {
        fun getResult(result: String)
    }

    public fun setInterface(translateInterface : TranslateInterface) {
        this.translateInterface = translateInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false

        _binding = FragmentRecordingDialogBinding.inflate(inflater, container, false)

        startSTT()

        return binding.root
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (_: java.lang.IllegalStateException) {

        }
    }

    private fun startSTT() {
        val ii = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireContext().packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        SpeechRecognizer.createSpeechRecognizer(requireContext()).apply {
            setRecognitionListener(recognitionListener())
            startListening(speechRecognizerIntent)
        }
    }

    private fun recognitionListener() = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {

        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            when (error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(
                    requireContext(),
                    "퍼미션 없음",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onResults(results: Bundle) {
            translateInterface.getResult(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0])
            Log.d("TAG", "onResults : ${results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]}")
            dismiss()
        }
    }
}