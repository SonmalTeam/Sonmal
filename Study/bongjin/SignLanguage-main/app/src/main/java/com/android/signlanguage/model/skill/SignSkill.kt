package com.android.signlanguage.model.skill

import kotlin.math.log

class SignSkill(val sign: Char, private var _step: Int = 0) {

    val step
        get() = _step

    fun increment() {
        if (_step + 1 <= maxSteps)
            _step++
    }

    val maxSteps = 43

    val skill: Double
        get() {
            var eq = (log((0.5 + _step.toDouble()) * 2, 1.16)) / 30
            if (eq > 1.0)
                eq = 1.0
            return eq
        }
}