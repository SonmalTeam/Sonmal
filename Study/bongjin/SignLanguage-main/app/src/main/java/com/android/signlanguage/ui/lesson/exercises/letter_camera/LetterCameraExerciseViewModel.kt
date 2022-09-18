package com.android.signlanguage.ui.lesson.exercises.letter_camera

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.android.signlanguage.FinishedListener
import com.android.signlanguage.model.Language
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter

class LetterCameraExerciseViewModel(sign: Char) : ViewModel(),
    FinishedListener {

    companion object {
        private const val TAG = "LetterCameraExerciseViewModel"
    }

    lateinit var signDetectionModel: Interpreter

    private val _finished = MutableLiveData<Boolean?>()
    override val finished: LiveData<Boolean?> = _finished

    private val _rightAnswer = MutableLiveData<Char>()
    val rightAnswer = Transformations.map(_rightAnswer) { it.toString() }

    var wrongPrediction: ((prediction: Char) -> Unit)? = null
    var rightPrediction: (() -> Unit)? = null

    var rightSignDetected = false

    init {
        _rightAnswer.value = sign
    }

    val isCameraAccessible = MutableLiveData(false)
    val cameraAccessErrorVisibility = Transformations.map(isCameraAccessible) {
        if (it) View.GONE else View.VISIBLE
    }

    val isLoading = MutableLiveData(false)
    val loadingVisibility = Transformations.map(isLoading) {
        if (it) View.VISIBLE else View.GONE
    }

    private val _continuousSignDetector =
        ContinuousSignDetector(20, 0.80, 750L)

    init {
        _continuousSignDetector.signDetected = {
            if (finished.value == null) {
                if (it == _rightAnswer.value) {
                    rightSignDetected = true
                    rightPrediction?.invoke()
                } else {
                    wrongPrediction?.invoke(it)
                }
            }
        }
    }

    fun handsCallback(input: Array<Array<FloatArray>>) {
        if (rightSignDetected)
            return

        val output = Array(1) { FloatArray(Language.maxLetters) }
        signDetectionModel.run(input, output)

        var predictedSignIndex = 0
        for (j in 0 until Language.maxLetters) {
            if (output[0][j] > output[0][predictedSignIndex])
                predictedSignIndex = j
        }

        if (output[0][predictedSignIndex] >= 0.80) {

            val predictedSign = 'A' + predictedSignIndex
            _continuousSignDetector.addPrediction(
                predictedSign,
                System.currentTimeMillis()
            )

            Log.d(TAG, "handsCallback: $predictedSign")
        }
    }

    fun finish() {
        _finished.value = true
    }
}

class LetterCameraExerciseViewModelFactory(val sign: Char) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LetterCameraExerciseViewModel(sign) as T
    }
}