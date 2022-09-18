package com.android.signlanguage.model.skill

import android.content.Context
import com.android.signlanguage.model.Language
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.io.File
import kotlin.random.Random

class UserSkill {

    companion object {

        private const val FILENAME = "user_skill"

        private var instance: UserSkill? = null

        fun getInstance(context: Context): UserSkill {
            if (instance == null) {
                instance = read(context)
            }
            return instance!!
        }

        /**
         * Call this only if you are sure the instance was acquired earlier.
         * Otherwise call getInstance(context) first
         * @exception NullPointerException if instance was not acquired earlier
         */
        fun requireInstance(): UserSkill {
            if (instance == null) {
                throw NullPointerException("Instance is null, you shouldn't call this method. Call 'getInstance(context)' first")
            }
            return instance!!
        }

        private fun read(context: Context): UserSkill {
            val file = File(context.filesDir, FILENAME)
            return if (file.exists()) {
                val content = file.readText()

                val moshi = Moshi.Builder().build()
                val jsonAdapter: JsonAdapter<UserSkill> =
                    moshi.adapter(UserSkill::class.java)

                jsonAdapter.fromJson(content)!!
            } else {
                val userSkill = UserSkill()
                userSkill
            }
        }

        fun useSavedVersion(context: Context) {
            instance = read(context)
        }

        fun save(context: Context) {
            val file = File(context.filesDir, FILENAME)
            if (!file.exists()) {
                file.createNewFile()
            }

            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<UserSkill> =
                moshi.adapter(UserSkill::class.java)

            val content = jsonAdapter.toJson(getInstance(context))
            file.writeText(content)
        }
    }

    private val _unlockedSigns: MutableList<SignSkill> = mutableListOf()
    val unlockedSignsCount
        get() = _unlockedSigns.size

    private var _points = 0
    val points
        get() = _points

    val pointsForNextLevel: Int
        get() {
            return PointsMilestones.milestones[PointsMilestones.getClosestIndex(points)!!]
        }

    fun cutPoints() {
        if (_points > PointsMilestones.max)
            _points = PointsMilestones.max
    }

    fun unlockSign(sign: Char) {
        if (_unlockedSigns.find { it.sign == sign } == null)
            _unlockedSigns += SignSkill(sign)
    }

    fun reset() {
        _points = 0
        _unlockedSigns.clear()
    }

    fun getRandomUnlockedSign(): Char {
        return generateWithProbabilities(_unlockedSigns).random()
    }

    fun getRandomUnlockedSignExcluding(sign: Char): Char {
        return generateWithProbabilities(_unlockedSigns.minus(_unlockedSigns.find { it.sign == sign }!!)).random()
    }

    private fun generateWithProbabilities(unlockedSigns: List<SignSkill>): MutableList<Char> {
        val result = mutableListOf<Char>()
        for (signSkill in unlockedSigns) {
            when {
                signSkill.skill < 0.4 -> result.addAll(MutableList(4) { signSkill.sign })
                signSkill.skill < 0.8 -> result.addAll(MutableList(2) { signSkill.sign })
                else -> result.add(signSkill.sign)
            }
        }
        return result
    }

    /**
     * Is current skill enough to learn new sign
     */
    fun isNewSignReady(): Boolean {
        fun percentage(v: Int): Double = v.toDouble() / _unlockedSigns.size

        val lowLearnedSigns = _unlockedSigns.count { it.skill < 0.4 }
        val highLearnedSigns = _unlockedSigns.count { it.skill > 0.8 }
        val mediumLearnedSigns = unlockedSignsCount - lowLearnedSigns - highLearnedSigns

        return (unlockedSignsCount < Language.maxLetters && lowLearnedSigns < 2)
    }

    fun upgrade(sign: Char) {
        _unlockedSigns.find { it.sign == sign }?.let {
            it.increment()
        }

        _points += Random.nextInt(75, 125)
        cutPoints()
    }

    fun addPointsForEndedLesson() {
        _points += 250
        cutPoints()
    }

    override fun toString(): String {
        return _unlockedSigns.joinToString { "${it.sign} - ${it.skill} (${it.step})" }
    }
}