package com.d202.sonmal.utils

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.graphics.Point
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.common.ApplicationClass.Companion.classes
import com.google.mediapipe.solutions.hands.HandsResult
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.acos
import kotlin.math.round
import kotlin.math.sqrt

private const val TAG = "Extension"
fun getDeviceSize(activity: Activity): Point {
    val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return windowManager.currentWindowMetricsPointCompat()
}

fun WindowManager.currentWindowMetricsPointCompat() : Point {
    // R(30) 이상
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val windowInsets = currentWindowMetrics.windowInsets
        var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
        windowInsets.displayCutout?.run {
            insets = Insets.max(
                insets,
                Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom)
            )
        }
        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom
        Point(
            currentWindowMetrics.bounds.width() - insetsWidth,
            currentWindowMetrics.bounds.height() - insetsHeight
        )
    } else {
        Point().apply {
            defaultDisplay.getSize(this)
        }
    }
}

fun translate(result : HandsResult): String{
    if (result.multiHandLandmarks().isEmpty()) {
        return ""
    }
    val landmarkList = result.multiHandLandmarks()[0].landmarkList
    val joint = Array(21){FloatArray(3)}
    for(i in 0..19) {
        joint[i][0] = landmarkList[i].x
        joint[i][1] = landmarkList[i].y
        joint[i][2] = landmarkList[i].z
    }

    val v1 = joint.slice(0..19).toMutableList()
    for(i in 4..16 step(4)) {
        v1[i] = v1[0]
    }
    var v2 = joint.slice(1..20)
    val v = Array(20) { FloatArray(3) }

    for(i in 0..19) {
        for(j in 0..2) {
            v[i][j] = v2[i][j] - v1[i][j]
        }
    }
    //Log.d(TAG, "onCreate: $v")

    for(i in 0..19) {
        val norm = sqrt(v[i][0] * v[i][0] + v[i][1] * v[i][1] + v[i][2] * v[i][2])
        for(j in 0..2) {
            v[i][j] /= norm
        }
    }
    //Log.d(TAG, "onCreate: $v")

    val tmpv1 = mutableListOf<FloatArray>()
    for(i in 0..18) {
        if(i != 3 && i != 7 && i != 11 && i != 15) {
            tmpv1.add(v[i])
        }
    }
    val tmpv2 = mutableListOf<FloatArray>()
    for(i in 1..19) {
        if(i != 4 && i != 8 && i != 12 && i != 16) {
            tmpv2.add(v[i])
        }
    }

    val einsum = FloatArray(15)
    for( i in 0..14) {
        einsum[i] = tmpv1[i][0] * tmpv2[i][0] + tmpv1[i][1] * tmpv2[i][1] + tmpv1[i][2] * tmpv2[i][2]
    }
    val angle = FloatArray(15)
    val data = FloatArray(15)
    for(i in 0..14) {
        angle[i] = Math.toDegrees(acos(einsum[i]).toDouble()).toFloat()
        data[i] = round(angle[i] * 100000) / 100000
    }

    val interpreter = getTfliteInterpreter("converted_model.tflite")
    val byteBuffer = ByteBuffer.allocateDirect(15*4).order(ByteOrder.nativeOrder())

    for(d in data) {
        byteBuffer.putFloat(d)
    }

    val modelOutput = ByteBuffer.allocateDirect(26*4).order(ByteOrder.nativeOrder())
    modelOutput.rewind()

    interpreter!!.run(byteBuffer,modelOutput)

    val outputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1,26), DataType.FLOAT32)
    outputFeature0.loadBuffer(modelOutput)

    // ByteBuffer to FloatBuffer
    val outputsFloatBuffer = modelOutput.asFloatBuffer()
    val outputs = mutableListOf<Float>()
    for(i in 1..26) {
        outputs.add(outputsFloatBuffer.get())
    }
//    Log.d(TAG, "outputs : $outputs")
    val sortedOutput = outputs.sortedDescending()
    val index = outputs.indexOf(sortedOutput[0])
//    Log.d(TAG, "translate: ${classes[index]}")
    return classes[index]
}

fun getTfliteInterpreter(path: String): Interpreter? {
    try {
        return ApplicationClass.interpreter
    }
    catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}