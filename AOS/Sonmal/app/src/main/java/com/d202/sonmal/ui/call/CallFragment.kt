package com.d202.sonmal.ui.call

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.common.OPENVIDU_SECRET
import com.d202.sonmal.common.OPENVIDU_URL
import com.d202.sonmal.databinding.FragmentCallBinding
import com.d202.sonmal.ui.call.viewmodel.CallViewModel
import com.d202.webrtc.openvidu.LocalParticipant
import com.d202.webrtc.openvidu.Session
import com.d202.webrtc.utils.CustomHttpClient
import com.d202.webrtc.websocket.CustomWebSocket
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
private const val REQUEST_CODE_PERMISSIONS = 10

class CallFragment : Fragment() {
    private lateinit var binding: FragmentCallBinding
    private val viewModel: CallViewModel by viewModels()

    private lateinit var session: Session
    private lateinit var httpClient: CustomHttpClient
    private var toggle = true
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var audioManager: AudioManager
    private val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        ).toTypedArray()

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

        userId = "id"
        userName = "name"


        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL

        binding.btnSwitchCamera.setOnClickListener {
            session.getLocalParticipant()!!.switchCamera()
        }

        binding.btnExit.setOnClickListener {
            leaveSession()
            findNavController().popBackStack()
        }

        binding.viewsContainer.setOnClickListener {
            resizeView()
        }
        binding.btnSpeakerMode.isActivated = false

        binding.btnSpeakerMode.setOnClickListener {
            it.isActivated = !it.isActivated
            audioManager.isSpeakerphoneOn = !audioManager.isSpeakerphoneOn
        }
        viewModel.setSurfaceViewRenderer(binding.localGlSurfaceView)
        viewModel.bitmap.observe(viewLifecycleOwner){
            binding.ivTest.setImageBitmap(it)
        }
        viewModel.getFrames()
    }

    private fun resizeView() {
        var width: Int
        var height: Int

        if (toggle) {
            width = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                90f, resources.displayMetrics
            ).toInt()
            height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                120f, resources.displayMetrics
            ).toInt()

        } else {
            width = RelativeLayout.LayoutParams.MATCH_PARENT
            height = RelativeLayout.LayoutParams.MATCH_PARENT
        }
        binding.peerContainerRemote.layoutParams = RelativeLayout.LayoutParams(width, height)

        toggle = !toggle
    }

    override fun onResume() {
        super.onResume()

        if (allPermissionsGranted()) {
            initViews()
            httpClient = CustomHttpClient(
                OPENVIDU_URL, "Basic " + Base64.encodeToString(
                    "OPENVIDUAPP:$OPENVIDU_SECRET".toByteArray(), Base64.DEFAULT
                ).trim()
            )
            Log.d(TAG, "onResume: CustomHttpClient")

            val sessionId = userId + "-session"
            getToken(sessionId)

        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun getToken(sessionId: String) {
        try {
            // Session Request
            val sessionBody: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                "{\"customSessionId\": \"$sessionId\"}"
            )
            httpClient.httpCall(
                "/openvidu/api/sessions",
                "POST",
                "application/json",
                sessionBody,
                object : Callback {
                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "responseString: " + response.body!!.string())

                        // Token Request
                        val tokenBody: RequestBody =
                            RequestBody.create(
                                "application/json; charset=utf-8".toMediaTypeOrNull(),
                                "{}"
                            )
                        httpClient.httpCall(
                            "/openvidu/api/sessions/$sessionId/connection",
                            "POST",
                            "application/json",
                            tokenBody,
                            object : Callback {
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
        // Initialize our session
        session = Session(sessionId, token, requireActivity() as AppCompatActivity, binding.viewsContainer)

        // Initialize our local participant and start local camera
        val participantName: String = userName
        val localParticipant =
            LocalParticipant(
                participantName,
                session,
                requireActivity().applicationContext,
                binding.localGlSurfaceView
            )
        localParticipant.startCamera()

        // Initialize and connect the websocket to OpenVidu Server
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
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                //시작
            } else {
                Toast.makeText(
                    requireContext(),
                    "권한 설정을 확인해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
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
        findNavController().popBackStack()
    }

    override fun onPause() {
        leaveSession()
        super.onPause()
    }
}