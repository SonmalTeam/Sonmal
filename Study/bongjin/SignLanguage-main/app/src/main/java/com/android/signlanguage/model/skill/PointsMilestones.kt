package com.android.signlanguage.model.skill

object PointsMilestones {
    val milestones = arrayListOf(
        1500,
        4500,
        8000,
        12500,
        21000,
        30500,
        42500,
        55000,
        70500,
        89000,
        112500,
        141000
    )

    val max: Int
        get() = milestones.last()

    fun getClosestIndex(points: Int): Int? {
        for ((index, ms) in milestones.withIndex()) {
            if (ms > points)
                return index
        }
        return null
    }
}