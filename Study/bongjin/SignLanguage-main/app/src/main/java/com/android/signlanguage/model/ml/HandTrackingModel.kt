package com.android.signlanguage.model.ml

import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mediapipe.components.CameraHelper
import com.google.mediapipe.components.CameraXPreviewHelper
import com.google.mediapipe.components.ExternalTextureConverter
import com.google.mediapipe.components.FrameProcessor
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.glutil.EglManager

class HandTrackingModel {

    companion object {
        private const val TAG = "HandTrackingModel"

        private const val BINARY_GRAPH_NAME = "hand_tracking_mobile_gpu.binarypb"
        private const val INPUT_VIDEO_STREAM_NAME = "input_video"
        private const val OUTPUT_VIDEO_STREAM_NAME = "output_video"
        private const val OUTPUT_LANDMARKS_STREAM_NAME = "hand_landmarks"
        private const val INPUT_NUM_HANDS_SIDE_PACKET_NAME = "num_hands"
        private const val NUM_HANDS = 1
        private val CAMERA_FACING = CameraHelper.CameraFacing.FRONT
        private const val FLIP_FRAMES_VERTICALLY = true
    }

    private lateinit var _previewFrameTexture: SurfaceTexture
    private lateinit var _processor: FrameProcessor
    private lateinit var _eglManager: EglManager
    private lateinit var _converter: ExternalTextureConverter
    private lateinit var _cameraHelper: CameraXPreviewHelper
    private lateinit var _previewDisplayView: SurfaceView

    private var _handTrackingCallback: ((Array<Array<FloatArray>>) -> Unit)? = null

    private var _isCameraLoaded = MutableLiveData(false)
    val isCameraLoaded: LiveData<Boolean> = _isCameraLoaded

    fun initializeConverter() {
        _converter = ExternalTextureConverter(
            _eglManager.context, 2
        )
        _converter.setFlipY(FLIP_FRAMES_VERTICALLY)
        _converter.setConsumer(_processor)
    }

    fun setupPreviewDisplayView(pdv: SurfaceView) {
        _previewDisplayView = pdv
        _previewDisplayView.visibility = View.GONE
        _previewDisplayView
            .holder
            .addCallback(
                object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        Log.d(TAG, "surface created")

                        _processor.videoSurfaceOutput.setSurface(holder.surface)
                    }

                    override fun surfaceChanged(
                        holder: SurfaceHolder,
                        format: Int,
                        width: Int,
                        height: Int
                    ) {
                        Log.d(TAG, "surface changed")
                        val viewSize = Size(width, height)
                        val displaySize = _cameraHelper.computeDisplaySizeFromViewSize(viewSize)
                        val isCameraRotated = _cameraHelper.isCameraRotated
                        _converter.setSurfaceTextureAndAttachToGLContext(
                            _previewFrameTexture,
                            if (isCameraRotated) displaySize.height else displaySize.width,
                            if (isCameraRotated) displaySize.width else displaySize.height
                        )
                        try {
                            _previewFrameTexture.updateTexImage()
                        } catch (exception: Exception) {}
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        Log.d(TAG, "surface destroyed")
                        _processor.videoSurfaceOutput.setSurface(null)
                    }
                })
    }

    fun startCamera(activity: Activity) {
        _cameraHelper = CameraXPreviewHelper()
        _cameraHelper.setOnCameraStartedListener { surfaceTexture ->
            Log.d(TAG, "camera started")
            _previewFrameTexture = surfaceTexture!!
            _previewDisplayView.visibility = View.VISIBLE
        }
        _cameraHelper.startCamera(
            activity, CAMERA_FACING, null, null
        )
    }

    fun initializeProcessor(context: Context?) {
        _eglManager = EglManager(null)
        _processor = FrameProcessor(
            context,
            _eglManager.nativeContext,
            BINARY_GRAPH_NAME,
            INPUT_VIDEO_STREAM_NAME,
            OUTPUT_VIDEO_STREAM_NAME
        )
        _processor
            .videoSurfaceOutput
            .setFlipY(FLIP_FRAMES_VERTICALLY)

        _processor.setOnWillAddFrameListener {
            if (_isCameraLoaded.value == false)
                _isCameraLoaded.postValue(true)
        }

        val packetCreator = _processor.packetCreator
        val inputSidePackets: MutableMap<String, Packet> = HashMap()
        inputSidePackets[INPUT_NUM_HANDS_SIDE_PACKET_NAME] = packetCreator.createInt32(NUM_HANDS)
        _processor.setInputSidePackets(inputSidePackets)

        _processor.addPacketCallback(
            OUTPUT_LANDMARKS_STREAM_NAME
        ) { packet: Packet ->
            handsCallback(packet)
        }
    }

    private fun handsCallback(packet: Packet) {
        val multiHandLandmarks =
            PacketGetter.getProtoVector(
                packet,
                LandmarkProto.NormalizedLandmarkList.parser()
            )
        val handLandmarks = multiHandLandmarks[0]

        _handTrackingCallback?.invoke(processHandLandmarks(handLandmarks))
    }

    private fun processHandLandmarks(handLandmarks: LandmarkProto.NormalizedLandmarkList): Array<Array<FloatArray>> {
        val input = Array(1) { Array(21) { FloatArray(3) } }

        for (i in 0..20) {
            val lm = handLandmarks.getLandmark(i)
            input[0][i][0] = lm.x
            input[0][i][1] = lm.y
            input[0][i][2] = lm.z
        }

        alignAxisLandmarks(input, 0)
        alignAxisLandmarks(input, 1)
        alignAxisLandmarks(input, 2)

        normalizePoints(input)

        return input
    }

    private fun alignAxisLandmarks(src: Array<Array<FloatArray>>, ax: Int) {
        var min = 100f
        for (i in 0..20) {
            val v = src[0][i][ax]
            if (v < min) {
                min = v
            }
        }
        for (i in 0..20) {
            src[0][i][ax] -= min
        }
    }

    private fun normalizePoints(src: Array<Array<FloatArray>>) {
        var max = -100f
        for (point in src[0]) {
            for (ax in point) {
                if (ax > max)
                    max = ax
            }
        }
        for (point in src[0]) {
            point[0] /= max
            point[1] /= max
            point[2] /= max
        }
    }

    /**
     * @param handsLandmarks Array(1 - hands) { Array(21 - landmarks) { FloatArray(3 - axes) } }
     */
    fun addCallback(callback: (Array<Array<FloatArray>>) -> Unit) {
        _handTrackingCallback = callback
    }

    fun closeConverter() {
        _previewDisplayView.visibility = View.GONE
        _converter.close()
    }

    fun close() {
        _converter.removeConsumer(_processor)
        _processor.close()
    }
}