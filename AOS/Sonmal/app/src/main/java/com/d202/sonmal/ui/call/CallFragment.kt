package com.d202.sonmal.ui.call

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.d202.sonmal.adapter.CallMacroPagingAdapter
import com.d202.sonmal.adapter.MacroPagingAdapter
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.common.OPENVIDU_SECRET
import com.d202.sonmal.common.OPENVIDU_URL
import com.d202.sonmal.common.REQUEST_CODE_PERMISSIONS
import com.d202.sonmal.databinding.FragmentCallBinding
import com.d202.sonmal.ui.call.viewmodel.CallViewModel
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel
import com.d202.sonmal.utils.HandsResultImageView
import com.d202.sonmal.utils.MainSharedPreference
import com.d202.sonmal.utils.translate
import com.d202.webrtc.openvidu.LocalParticipant
import com.d202.webrtc.openvidu.Session
import com.d202.webrtc.utils.CustomHttpClient
import com.d202.webrtc.websocket.CustomWebSocket
import com.google.mediapipe.components.CameraHelper
import com.google.mediapipe.solutions.hands.HandLandmark
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.google.mediapipe.solutions.hands.HandsResult
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.EglBase
import java.io.IOException


private const val TAG = "CallFragment"
private val CAMERA_FACING = CameraHelper.CameraFacing.FRONT

class CallFragment : Fragment() {
    //View
    private lateinit var binding: FragmentCallBinding
    private val viewModel: CallViewModel by viewModels()
    private val macroViewModel: MacroViewModel by viewModels()
    private lateinit var macroAdapter: CallMacroPagingAdapter

    //WebRTC
    private lateinit var session: Session
    private lateinit var httpClient: CustomHttpClient
    private var toggle = true
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var audioManager: AudioManager

    //MediaPipe
    private lateinit var hands: Hands
    private lateinit var imageView: HandsResultImageView
    private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS).toTypedArray()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        userId = MainSharedPreference(requireContext()).token.toString()
        userName = MainSharedPreference(requireContext()).token.toString()

        initView()
        initViewModel()
    }

    private fun setupStaticImageModePipeline() {
        hands = Hands(
            requireContext(),
            HandsOptions.builder()
                .setStaticImageMode(true)
                .setMaxNumHands(1)
                .setRunOnGpu(true)
                .build()
        )

        // Connects MediaPipe Hands solution to the user-defined HandsResultImageView.
        hands.setResultListener { handsResult ->
//            logWristLandmark(handsResult,  /*showPixelValues=*/true)
            imageView.setHandsResult(handsResult)
            requireActivity().runOnUiThread(Runnable { imageView.update() })
            val result = translate(handsResult)
            if(result.isNotEmpty()) {
                viewModel.setTranslateText(result)
            }
        }
        hands.setErrorListener { message, e ->
            Log.e(
                TAG,
                "MediaPipe Hands error:$message"
            )
        }

        val viewGroup = binding.peerContainerRemote
        imageView = HandsResultImageView(requireContext())
        imageView.setImageDrawable(null)
        viewGroup.addView(imageView)
        binding.tvTranslateText.bringToFront()
        binding.tvChatTop.bringToFront()
        binding.tvChatBottom.bringToFront()
        imageView.setVisibility(View.VISIBLE)
    }


    private fun initView(){
//        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        audioManager.mode = AudioManager.MODE_NORMAL
        macroAdapter = CallMacroPagingAdapter()
        macroAdapter.apply {
            onItemMacroClickListener = object : CallMacroPagingAdapter.OnItemMacroClickListener{
                override fun onClick(title: String) {
                    binding.etChat.setText("${binding.etChat.text} ${title} ")
                }
            }
        }

        binding.apply {
            lifecycleOwner = this@CallFragment
            vm = viewModel

            ivCameraSwitch.setOnClickListener {
                session.getLocalParticipant()!!.switchCamera()
            }
            ivCallEnd.setOnClickListener {
                findNavController().popBackStack()
            }
            viewsContainer.setOnClickListener {
                resizeView()
            }
            ivSpeakerOn.isActivated = false
            ivSpeakerOn.setOnClickListener {
                it.isActivated = !it.isActivated
                audioManager.isSpeakerphoneOn = !audioManager.isSpeakerphoneOn
            }
            recyclerMacro.apply {
                adapter = macroAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            btnSend.setOnClickListener {
                viewModel.sendMessage(etChat.text.toString(), userName)
                etChatInput.setText("${etChatInput.text}\n${etChat.text}")
                etChat.setText("")
                etChatInput.movementMethod = ScrollingMovementMethod.getInstance()
                etChatInput.setSelection(etChatInput.text.length, etChatInput.text.length)
            }
            recyclerMacro.apply {
                adapter = macroAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    private fun initViewModel(){
        viewModel.apply {
            initFirebaseDatabase(userName)
            setSurfaceViewRenderer(binding.remoteGlSurfaceView)
            initTTS(requireContext())
            startSTT(requireContext(), userName)
            bitmap.observe(viewLifecycleOwner){
                hands.send(it)
            }
            chatList.observe(viewLifecycleOwner){
                binding.apply {
                    if(it.size > 0){
                        tvChatBottom.text = it[it.size - 1].message
                    }
                    if(it.size > 1){
                        tvChatTop.text = it[it.size - 2].message
                    }
                }
            }
            getRemoteFrames()
        }
        macroViewModel.apply {
            getPagingMacroListValue(0)
            pagingMacroList.observe(viewLifecycleOwner){
                macroAdapter.submitData(this@CallFragment.lifecycle, it)
            }
        }
        setupStaticImageModePipeline()

    }

    private fun resizeView() {
        val displaymetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        val deviceWidth = displaymetrics.widthPixels
        val deviceHeight = deviceWidth
        binding.peerContainerRemote.layoutParams.width = deviceWidth
        binding.peerContainerRemote.layoutParams.height = deviceWidth
    }

    override fun onResume() {
        super.onResume()
        resizeView()
        initViews()

        httpClient = CustomHttpClient(OPENVIDU_URL, "Basic " + Base64.encodeToString("OPENVIDUAPP:$OPENVIDU_SECRET".toByteArray(), Base64.DEFAULT).trim())
        Log.d(TAG, "onResume: CustomHttpClient")
        val sessionId = "-session"
        getToken(sessionId)
    }

    private fun getToken(sessionId: String) {
        try {
            val sessionBody: RequestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), "{\"customSessionId\": \"$sessionId\"}")
            httpClient.httpCall("/openvidu/api/sessions", "POST", "application/json", sessionBody, object : Callback {
                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "responseString: " + response.body!!.string())
                        // Token Request
                        val tokenBody: RequestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), "{}")
                        httpClient.httpCall("/openvidu/api/sessions/$sessionId/connection", "POST", "application/json", tokenBody, object : Callback {
                                override fun onResponse(call: Call, response: Response) {
                                    var responseString: String? = null
                                    try {
                                        responseString = response.body!!.string()
                                    } catch (e: IOException) {
                                        Log.e(TAG, "Error getting body", e)
                                    }
                                    Log.d(TAG, "responseString2: $responseString")
                                    var tokenJsonObject: JSONObject? = null
                                    var token: String? = null
                                    try {
                                        tokenJsonObject = JSONObject(responseString)
                                        token = tokenJsonObject.getString("token")
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                    getTokenSuccess(token!!, sessionId)
                                }

                                override fun onFailure(call: Call, e: IOException) {
                                    Log.e(TAG, "Error POST /api/tokens", e)
                                    viewToDisconnectedState()
                                }
                            })
                    }
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "Error POST /api/sessions", e)
                        viewToDisconnectedState()
                    }
                })
        } catch (e: IOException) {
            Log.e(TAG, "Error getting token", e)
            e.printStackTrace()
            viewToDisconnectedState()
        }
    }

    private fun initViews() {
        val rootEgleBase = EglBase.create()
        binding.localGlSurfaceView.init(rootEgleBase.eglBaseContext, null)
        binding.localGlSurfaceView.setMirror(true)
        binding.localGlSurfaceView.setEnableHardwareScaler(true)
        binding.localGlSurfaceView.setZOrderMediaOverlay(true)
    }

    private fun getTokenSuccess(token: String, sessionId: String) {
        session = Session(sessionId, token, requireActivity() as AppCompatActivity, binding.viewsContainer)
        val participantName: String = userName
        val localParticipant =
            LocalParticipant(
                participantName,
                session,
                requireActivity().applicationContext,
                binding.localGlSurfaceView
            )
        localParticipant.startCamera()
        startWebSocket()
    }
    fun viewToDisconnectedState() {
        requireActivity().runOnUiThread {
            binding.localGlSurfaceView.clearImage()
            binding.localGlSurfaceView.release()
        }
    }
    private fun startWebSocket() {
        val webSocket = CustomWebSocket(session, OPENVIDU_URL, requireActivity() as AppCompatActivity)
        webSocket.execute()
        session.setWebSocket(webSocket)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {

            } else {
                Toast.makeText(requireContext(), "권한 설정을 확인해주세요.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun leaveSession() {
        this.session.leaveSession()
        this.httpClient.dispose()
        requireActivity().runOnUiThread {
            binding.localGlSurfaceView.clearImage()
            binding.localGlSurfaceView.release()
        }

    }

    override fun onPause() {
        super.onPause()
        leaveSession()
        viewModel.stopSTT()
    }

    private fun logWristLandmark(result: HandsResult, showPixelValues: Boolean) {
        if (result.multiHandLandmarks().isEmpty()) {
            return
        }
        val wristLandmark = result.multiHandLandmarks()[0].landmarkList[HandLandmark.WRIST]
        // For Bitmaps, show the pixel values. For texture inputs, show the normalized coordinates.
        if (showPixelValues) {
            val width = result.inputBitmap().width
            val height = result.inputBitmap().height
            Log.i(
                TAG, String.format(
                    "MediaPipe Hand wrist coordinates (pixel values): x=%f, y=%f",
                    wristLandmark.x * width, wristLandmark.y * height
                )
            )
        } else {
            Log.i(
                TAG, String.format(
                    "MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
                    wristLandmark.x, wristLandmark.y
                )
            )
        }
        if (result.multiHandWorldLandmarks().isEmpty()) {
            return
        }
        val wristWorldLandmark =
            result.multiHandWorldLandmarks()[0].landmarkList[HandLandmark.WRIST]
        Log.i(
            TAG, String.format(
                "MediaPipe Hand wrist world coordinates (in meters with the origin at the hand's"
                        + " approximate geometric center): x=%f m, y=%f m, z=%f m",
                wristWorldLandmark.x, wristWorldLandmark.y, wristWorldLandmark.z
            )
        )
    }

}