package com.d202.sonmal.ui.call

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.R
import com.d202.sonmal.common.*
import com.d202.sonmal.databinding.FragmentCallBinding
import com.d202.sonmal.ui.call.viewmodel.CallViewModel
import com.d202.webrtc.openvidu.LocalParticipant
import com.d202.webrtc.openvidu.Session
import com.d202.webrtc.utils.CustomHttpClient
import com.d202.webrtc.websocket.CustomWebSocket
import com.google.mediapipe.components.CameraHelper
import com.google.mediapipe.components.CameraXPreviewHelper
import com.google.mediapipe.components.ExternalTextureConverter
import com.google.mediapipe.components.FrameProcessor
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.glutil.EglManager
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
    private lateinit var binding: FragmentCallBinding
    private val viewModel: CallViewModel by viewModels()

    private lateinit var session: Session
    private lateinit var httpClient: CustomHttpClient
    private var toggle = true
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var audioManager: AudioManager

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private var previewFrameTexture: SurfaceTexture? = null
    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private var previewDisplayView: SurfaceView? = null
    // Creates and manages an {@link EGLContext}.
    private var eglManager: EglManager? = null
    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    private var processor: FrameProcessor? = null
    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private var converter: ExternalTextureConverter? = null
    // Handles camera access via the {@link CameraX} Jetpack support library.
    private var cameraHelper: CameraXPreviewHelper? = null
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

        initView()
        initViewModel()
        initSurface()
    }

    private fun initSurface(){
        previewDisplayView = SurfaceView(requireContext())
        setupPreviewDisplayView()
        AndroidAssetUtil.initializeNativeAssetManager(requireContext())
        eglManager = EglManager(null)
        processor = FrameProcessor(requireContext(), eglManager!!.nativeContext, BINARY_GRAPH_NAME, INPUT_VIDEO_STREAM_NAME, OUTPUT_VIDEO_STREAM_NAME)
        var inputSidePackets = mutableMapOf<String, Packet>()
        processor!!.setInputSidePackets(inputSidePackets)

        processor!!.videoSurfaceOutput.setFlipY(FLIP_FRAMES_VERTICALLY)
        processor!!.addPacketCallback(OUTPUT_LANDMARKS_STREAM_NAME) { packet: Packet ->
//            Log.d(TAG, "Received multi-hand landmarks packet.")
            val multiHandLandmarks =
                PacketGetter.getProtoVector(
                    packet,
                    LandmarkProto.NormalizedLandmarkList.parser()
                )
//            Log.d(TAG, "onCreate: ${PacketGetter.getFloat32Vector(packet)} ")
//            if (!multiHandLandmarks.isEmpty())
                Log.d(TAG, "[TS:" + packet.timestamp + "] " + getMultiHandLandmarksDebugString(multiHandLandmarks))
//            pointToVector(multiHandLandmarks)
        }
    }

    private fun setupPreviewDisplayView() {
        previewDisplayView!!.visibility = View.GONE
        val viewGroup = binding.peerContainerRemote
        viewGroup.addView(previewDisplayView)
        previewDisplayView!!.getHolder().addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        processor!!.videoSurfaceOutput.setSurface(holder.surface)
                        Log.d(TAG, "surfaceCreated: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@${holder}")
                    }
                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        // (Re-)Compute the ideal size of the camera-preview display (the area that
                        // the camera-preview frames get rendered onto, potentially with scaling and
                        // rotation) based on the size of the SurfaceView that contains the display.
                        Log.d(TAG, "surfaceChanged: ########################################################################${holder.surface}\n${holder.surfaceFrame}\n")
                        val viewSize = Size(width, height)
//                        val displaySize = cameraHelper!!.computeDisplaySizeFromViewSize(viewSize)
                        // Connect the converter to the camera-preview frames as its input (via
                        // previewFrameTexture), and configure the output width and height as the
                        // computed display size.

//                        converter!!.setSurfaceTextureAndAttachToGLContext(previewFrameTexture, viewSize.width, viewSize.height)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        processor!!.videoSurfaceOutput.setSurface(null)
                    }
                })
    }

    private fun initView(){
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL

        binding.apply {
            btnSwitchCamera.setOnClickListener {
                session.getLocalParticipant()!!.switchCamera()
            }

            btnExit.setOnClickListener {
                leaveSession()
                findNavController().popBackStack()
            }

            viewsContainer.setOnClickListener {
                resizeView()
            }
            btnSpeakerMode.isActivated = false

            btnSpeakerMode.setOnClickListener {
                it.isActivated = !it.isActivated
                audioManager.isSpeakerphoneOn = !audioManager.isSpeakerphoneOn
            }
        }
    }

    private fun initViewModel(){
        viewModel.apply {
            setSurfaceViewRenderer(binding.remoteGlSurfaceView)
            bitmap.observe(viewLifecycleOwner){
                binding.ivTest.setImageBitmap(it)
                previewDisplayView!!.visibility = View.VISIBLE
            }
            getFrames()

        }

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

            converter = ExternalTextureConverter(eglManager!!.context)
            converter!!.setFlipY(FLIP_FRAMES_VERTICALLY)
            converter!!.setConsumer(processor)


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

    private fun getMultiHandLandmarksDebugString(multiHandLandmarks: List<LandmarkProto.NormalizedLandmarkList>): String {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks"
        }
        var multiHandLandmarksStr = "Number of hands detected: " + multiHandLandmarks.size + "\n"
        for ((handIndex, landmarks) in multiHandLandmarks.withIndex()) {
            multiHandLandmarksStr += "\t#Hand landmarks for hand[" + handIndex + "]: " + landmarks.landmarkCount + "\n"
            for ((landmarkIndex, landmark) in landmarks.landmarkList.withIndex()) {
                multiHandLandmarksStr += ("\t\tLandmark [" + landmarkIndex + "]: (" + landmark.x + ", " + landmark.y + ", " + landmark.z + ")\n")
            }
        }
        return multiHandLandmarksStr
    }

    companion object {
        init {
            System.loadLibrary("mediapipe_jni")
            System.loadLibrary("opencv_java3")
        }
    }
}