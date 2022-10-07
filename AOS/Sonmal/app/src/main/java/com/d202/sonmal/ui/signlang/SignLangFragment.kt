package com.d202.sonmal.ui.signlang

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.d202.sonmal.R
import com.d202.sonmal.adapter.SignMacroPagingAdapter
import com.d202.sonmal.databinding.FragmentSignLangBinding
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel
import com.d202.sonmal.utils.translate
import com.google.mediapipe.solutioncore.CameraInput
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.concurrent.thread

class SignLangFragment : Fragment(), TextToSpeech.OnInitListener {
    private val TAG = "SIGN_LANG_FRAGMENT"

    private lateinit var binding : FragmentSignLangBinding
    private val macroViewModel: MacroViewModel by viewModels()
    private lateinit var macroAdapter: SignMacroPagingAdapter

    private lateinit var tts : TextToSpeech
    private lateinit var hands : Hands
    private lateinit var cameraInput: CameraInput
    private lateinit var glSurfaceView: SolutionGlSurfaceView<HandsResult>

    private var charlist = mutableListOf<String>()
    private var startTime = 0L
    private var isStarted = false
    lateinit var hangulMaker: HangulMaker

    private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_NETWORK_STATE).toTypedArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignLangBinding.inflate(inflater, container, false)


        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                setupStreamingModePipeline()

                cameraInput = CameraInput(activity)
                cameraInput.setNewFrameListener { hands.send(it) }
                glSurfaceView.post { startCamera() }
                glSurfaceView.visibility = View.VISIBLE

                initAdapter()
                initViewModel()
                initView()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(requireContext(), "권한을 다시 설정해주세요!", Toast.LENGTH_SHORT).show()
            }

        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setPermissions(*REQUIRED_PERMISSIONS)
            .check()

        initTTS()
        return binding.root
    }

    private fun initTTS() {
        tts = TextToSpeech(requireContext(), this)
        tts.setPitch(1f)
        tts.setSpeechRate(1f)
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

            override fun onError(p0: String?) {}
        })
    }

    private fun initView() {
        binding.apply {
            tvSttResult.movementMethod = ScrollingMovementMethod()

            etNowTranslate.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    macroViewModel.getPagingMacroListValue(binding.etNowTranslate.text.toString())
                }
            })

            test.setOnClickListener {
                ivRecord.setImageResource(R.drawable.record_start)
                tvLiveTranslate.text = ""
                etNowTranslate.text.clear()
                hangulMaker.clear()
                isStarted = false
            }

            ivRecord.setOnClickListener {
                isStarted = !isStarted
                if(isStarted) {
                    val inputConnection: InputConnection = binding.etNowTranslate.onCreateInputConnection(EditorInfo())
                    hangulMaker = HangulMaker(inputConnection)

                    ivRecord.setImageResource(R.drawable.record_stop)
                    startTime = System.currentTimeMillis()
                    thread(start = true) {
                        while(isStarted) {
                            binding.progressBar.progress = (System.currentTimeMillis() - startTime).toInt()
                            Thread.sleep(1)
                        }
                        binding.progressBar.progress = 0
                    }
                }
                else {
                    ivRecord.setImageResource(R.drawable.record_start)
                    tvLiveTranslate.text = ""
                    hangulMaker.clear()
                }
                charlist.clear()
            }

            ltSpeak.setOnClickListener {
                speakOut(binding.etNowTranslate.text.toString())
            }

            ltRecord.setOnClickListener {
                startSTT()
            }
        }
    }

    private fun initAdapter() {
        macroAdapter = SignMacroPagingAdapter()
        macroAdapter.apply {
            onItemMacroClickListener = object : SignMacroPagingAdapter.OnItemMacroClickListener{
                override fun onClick(content: String) {
                    speakOut(content)
                }
            }
        }
        binding.rvMacro.apply {
            adapter = macroAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initViewModel() {
        macroViewModel.apply {
            pagingMacroSearchList.observe(viewLifecycleOwner){
                macroAdapter.submitData(this@SignLangFragment.lifecycle, it)
            }
        }
    }

    private fun setupStreamingModePipeline() {
        hands = Hands(
            requireContext(),
            HandsOptions.builder()
                .setStaticImageMode(false)
                .setMaxNumHands(1)
                .setRunOnGpu(true)
                .build()
        )
        hands.setErrorListener { message, e -> Log.e(TAG, "MediaPipe Hands error: $message") }

        cameraInput = CameraInput(activity as Activity)
        cameraInput.setNewFrameListener { hands.send(it) }

        glSurfaceView = SolutionGlSurfaceView(requireContext(), hands.glContext, hands.glMajorVersion)
        glSurfaceView.setSolutionResultRenderer(HandsResultGlRenderer())
        glSurfaceView.setRenderInputImage(true)

        hands.setResultListener {
            translateResult(it)
            glSurfaceView.setRenderData(it)
            glSurfaceView.requestRender()
        }

        glSurfaceView.post(this::startCamera)

        binding.previewDisplayLayout.apply {
            removeAllViewsInLayout()
            addView(glSurfaceView)
            glSurfaceView.visibility = View.VISIBLE
            addView(binding.tvLiveTranslate)
            requestLayout()
        }
    }

    private fun startCamera() {
        cameraInput.start(
            activity as Activity,
            hands.glContext,
            CameraInput.CameraFacing.FRONT,
            glSurfaceView.width,
            glSurfaceView.height
        )
    }

    private fun translateResult(result : HandsResult){
        if (result.multiHandLandmarks().isEmpty() || !isStarted) {
            return
        }
        val translatedText = translate(result)
        (activity as Activity).runOnUiThread {
            binding.tvLiveTranslate.text = translatedText
        }
         if(System.currentTimeMillis() - startTime >= 2000) {
            startTime = System.currentTimeMillis()
            hangulMaker.commit(translatedText[0])
        }
    }

    private fun loadModelFile(activity: Activity, path: String): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun speakOut(content: String) {
        tts.speak(content, TextToSpeech.QUEUE_ADD, null, "id1")
        binding.tvNextTranslate.text = content
        binding.etNowTranslate.text.clear()
    }

    override fun onInit(p0: Int) {
        if(p0 == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN)
        }
    }

    private fun startSTT() {
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
            binding.ltRecord.playAnimation()
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            when(error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(requireContext(), "퍼미션 없음", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResults(results: Bundle) {
            binding.ltRecord.progress = 0f
            binding.ltRecord.pauseAnimation()
            binding.tvSttResult.text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
        }
    }
}