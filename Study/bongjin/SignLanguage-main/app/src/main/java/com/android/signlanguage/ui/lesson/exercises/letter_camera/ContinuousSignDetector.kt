package com.android.signlanguage.ui.lesson.exercises.letter_camera

import android.util.Log
import java.util.*

class ContinuousSignDetector(
    val maxPredictions: Int,
    val detectionPercentageFilter: Double,
    val resetTriggerDuration: Long
) {
    companion object {
        private const val TAG = "ContinuousSignDetector"
    }

    private var _predictions = LinkedList<Char>()
    private var _lastPredictionTime: Long = 0 // ms
    private var _lastPrediction: Char? = null

    var signDetected: ((sign: Char) -> Unit)? = null

    fun addPrediction(sign: Char, time: Long) {
        if (_lastPredictionTime != 0L && time - _lastPredictionTime > resetTriggerDuration) {
            reset()
        }
        _predictions.add(sign)
        _lastPredictionTime = time

        if (_predictions.size > maxPredictions) {
            _predictions.poll()
        }

        val signs = countSigns()
        if (signs.isNotEmpty()) {
            val mostFrequentSign = signs[0].first
            if (_predictions.size == maxPredictions) {
                val percentage = signs[0].second / _predictions.size
                if (percentage >= detectionPercentageFilter) {
                    if (_lastPrediction != mostFrequentSign) {
                        signDetected?.invoke(mostFrequentSign)
                        _lastPrediction = mostFrequentSign
                    }
                }
            }
        }
    }

    private fun countSigns(): List<Pair<Char, Int>> {
        val result = hashMapOf<Char, Int>()

        for (sign in _predictions) {
            result[sign]?.let {
                result[sign] = it + 1
            } ?: run {
                result.put(sign, 1)
            }
        }

        return result.toList().sortedByDescending { (k, v) -> v }
    }

    private fun reset() {
        _predictions.clear()
        _lastPredictionTime = 0
        _lastPrediction = null
    }
}