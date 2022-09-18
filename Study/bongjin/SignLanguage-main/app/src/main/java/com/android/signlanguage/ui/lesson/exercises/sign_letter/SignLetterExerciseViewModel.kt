package com.android.signlanguage.ui.lesson.exercises.sign_letter

import androidx.lifecycle.*
import com.android.signlanguage.FinishedListener
import com.android.signlanguage.model.Language
import com.android.signlanguage.model.skill.UserSkill
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SignLetterExerciseViewModel(sign: Char) : ViewModel(), FinishedListener {
    companion object {
        private const val POSSIBLE_ANSWERS = 4
    }

    private val _finished = MutableLiveData<Boolean?>()
    override val finished: LiveData<Boolean?> = _finished

    private val _rightAnswerIndex: Int

    private var _possibleAnswers: List<MutableLiveData<Char>> =
        List(POSSIBLE_ANSWERS) { MutableLiveData() }

    init {
        _possibleAnswers[0].value = sign
        for (i in 1 until POSSIBLE_ANSWERS) {
            var nextPossibleSign: Char
            val userSkill = UserSkill.requireInstance()
            if (userSkill.unlockedSignsCount < POSSIBLE_ANSWERS)
                throw Exception("This exercise requires $POSSIBLE_ANSWERS unlocked signs, but ${userSkill.unlockedSignsCount} was provided")
            do {
                nextPossibleSign = userSkill.getRandomUnlockedSign()
            } while (_possibleAnswers.indexOfFirst { it.value == nextPossibleSign } != -1)
            _possibleAnswers[i].value = nextPossibleSign
        }
        _possibleAnswers = _possibleAnswers.shuffled()
        _rightAnswerIndex = _possibleAnswers.indexOfFirst { it.value == sign }
    }

    val possibleAnswer1 = Transformations.map(_possibleAnswers[0]) { it.toString() }
    val possibleAnswer2 = Transformations.map(_possibleAnswers[1]) { it.toString() }
    val possibleAnswer3 = Transformations.map(_possibleAnswers[2]) { it.toString() }
    val possibleAnswer4 = Transformations.map(_possibleAnswers[3]) { it.toString() }

    val rightAnswer: LiveData<Char> = _possibleAnswers[_rightAnswerIndex]

    var isAnswerBlocked = false

    var showAnswerResults: ((rightAnswer: Int, answer: Int) -> Unit)? = null

    fun answer(signIndex: Int) {
        if (!isAnswerBlocked) {
            isAnswerBlocked = true
            showAnswerResults?.invoke(_rightAnswerIndex, signIndex)
        }
    }

    fun finish(rightAnswer: Boolean) {
        _finished.value = rightAnswer
    }
}

class SignLetterExerciseViewModelFactory(val sign: Char) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignLetterExerciseViewModel(sign) as T
    }
}