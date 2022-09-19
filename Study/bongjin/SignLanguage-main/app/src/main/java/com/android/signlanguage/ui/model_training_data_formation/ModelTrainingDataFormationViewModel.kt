package com.android.signlanguage.ui.model_training_data_formation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.lang.StringBuilder
import java.util.*

class ModelTrainingDataFormationViewModel : ViewModel() {
    private val _signsInQueue = MutableLiveData(0)

    private val _readySigns = LinkedList<Array<FloatArray>>()

    val readySignsText: String
        get() {
            val result = StringBuilder()
            for (sign in _readySigns) {
                for (landmark in sign) {
                    result.append("${landmark[0]},${landmark[1]},${landmark[2]}\n")
                }
            }
            return result.toString()
        }

    val captureButtonText = Transformations.map(_signsInQueue) {
        "Capture (${_readySigns.size})"
    }

    fun handsCallback(hand: Array<Array<FloatArray>>) {
        if (_signsInQueue.value!! > 0) {
            _readySigns += hand[0]
            _signsInQueue.postValue(_signsInQueue.value!! - 1)
        }
    }

    fun capture() {
        _signsInQueue.value = _signsInQueue.value!! + 1
    }
}