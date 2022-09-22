package com.d202.sonmal.common

import com.google.mediapipe.components.CameraHelper

/*
WebRTC
 */
const val SERVER_URL = "https://d202.kro.kr/api" //
const val OPENVIDU_URL = "https://d202.kro.kr:5443"
const val OPENVIDU_SECRET = "MY_SECRET"
const val REQUEST_CODE_PERMISSIONS = 10

/*
MediaPipe
 */
const val BINARY_GRAPH_NAME = "multihandtrackinggpu.binarypb"
const val INPUT_VIDEO_STREAM_NAME = "input_video"
const val OUTPUT_VIDEO_STREAM_NAME = "output_video"
const val OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks"
const val FLIP_FRAMES_VERTICALLY = true
