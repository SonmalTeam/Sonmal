package com.android.signlanguage.ui.lesson

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.android.signlanguage.FinishedListener
import com.android.signlanguage.model.Language
import com.android.signlanguage.model.skill.UserSkill
import com.android.signlanguage.ui.lesson.exercises.letter_camera.LetterCameraExerciseFragment
import com.android.signlanguage.ui.lesson.exercises.letter_sign.LetterSignExerciseFragment
import com.android.signlanguage.ui.lesson.exercises.sign_letter.SignLetterExerciseFragment
import com.android.signlanguage.ui.lesson.lesson_finished.LessonFinishedFragment
import com.android.signlanguage.ui.lesson.new_sign.NewSignFragment
import java.util.*

class LessonViewModel(
    var currentScreenChanged: ((lessonScreen: Fragment) -> Unit)? = null
) :
    ViewModel(), FinishedListener {

    companion object {
        private const val TAG = "LessonViewModel"

        const val MIN_SIGNS_FOR_LEARNING = 2
        const val EXERCISES_IN_LESSON = 10
        // New signs can't be shown after this point in lesson
        const val EXERCISES_FOR_NEW_SIGN = EXERCISES_IN_LESSON * 0.6

        fun filterExercises(): List<Class<out Any>> {
            return ExerciseConverter.exercises.filter {
                val exercise = ExerciseConverter.extractRules(it)
                UserSkill.requireInstance().unlockedSignsCount >= exercise.unlockedSignsRequired
            }
        }
    }

    private var _currentScreen = MutableLiveData<Fragment>()
    val currentScreen: LiveData<Fragment> = _currentScreen

    private val _finished = MutableLiveData<Boolean?>()
    override val finished: LiveData<Boolean?> = _finished

    private val _delayedScreens = LinkedList<Fragment>()

    private var _doneExercises = 0
    private val _doneExercisesSuccessfully = MutableLiveData(0)

    var exitPrompted: (() -> Unit)? = null
    var openHelpWindowPrompted: (() -> Unit)? = null

    val progress: LiveData<Int> = Transformations.map(_doneExercisesSuccessfully) {
        (it.toDouble() / EXERCISES_IN_LESSON.toDouble() * 100.0).toInt()
    }

    private val _userSkill = UserSkill.requireInstance()

    init {
        startNextScreen()
    }

    fun openHelpWindow() {
        openHelpWindowPrompted?.invoke()
    }

    fun promptExit() {
        exitPrompted?.invoke()
    }

    fun finish() {
        _finished.value = false
    }

    fun startNextScreen(prevExerciseFailed: Boolean = false) {
        if (_currentScreen.value != null && _currentScreen.value!! is Exercise) {
            _doneExercises++
            if (!prevExerciseFailed) {
                _doneExercisesSuccessfully.value = _doneExercisesSuccessfully.value!! + 1
                _userSkill.upgrade((_currentScreen.value as Exercise).sign)
            }
        }

        if (prevExerciseFailed) {
            _delayedScreens.add(currentScreen.value!!)
        }

        if (currentScreen.value != null && currentScreen.value!!::class.java == LessonFinishedFragment::class.java) {
            _finished.value = true
            return
        }

        val nextScreen = getNextExercise()

        _currentScreen.value = nextScreen
        currentScreenChanged?.invoke(nextScreen)
    }

    private fun getNextExercise(): Fragment {
        if (_doneExercisesSuccessfully.value!! >= EXERCISES_IN_LESSON) {
            return LessonFinishedFragment()
        }

        if (currentScreen.value != null && currentScreen.value!!::class.java == NewSignFragment::class.java) {
            val sign = (currentScreen.value!! as NewSignFragment).sign
            return LetterCameraExerciseFragment.newInstance(sign)
        }

        if (_userSkill.unlockedSignsCount < MIN_SIGNS_FOR_LEARNING
            || (_doneExercises < EXERCISES_FOR_NEW_SIGN && _userSkill.isNewSignReady())
        ) {
            return instantiateNewSignFragment()
        }

        if (_doneExercises >= EXERCISES_IN_LESSON) {
            val nextDelayedScreen = _delayedScreens.poll()

            return if (nextDelayedScreen != null) {
                if (_delayedScreens.isEmpty()) {
                    val sign = extractSign(nextDelayedScreen)
                    instantiateExerciseFragment(nextDelayedScreen::class.java, sign)
                } else {
                    nextDelayedScreen
                }
            } else {
                LessonFinishedFragment()
            }
        }

        return getRandomExercise()
    }

    private fun instantiateExerciseFragment(
        Class: Class<out Any>,
        sign: Char
    ): Fragment {
        return when (Class) {
            LetterCameraExerciseFragment::class.java -> {
                LetterCameraExerciseFragment.newInstance(sign)
            }
            SignLetterExerciseFragment::class.java -> {
                SignLetterExerciseFragment.newInstance(sign)
            }
            LetterSignExerciseFragment::class.java -> {
                LetterSignExerciseFragment.newInstance(sign)
            }
            else -> throw NotImplementedError()
        }
    }

    private fun instantiateNewSignFragment(): NewSignFragment {
        val newSign = Language.getLetter(_userSkill.unlockedSignsCount)
        _userSkill.unlockSign(newSign)
        return NewSignFragment.newInstance(newSign)
    }

    private fun getRandomExercise(): Fragment {
        val sign =
            if (_currentScreen.value != null && _currentScreen.value!! is Exercise) {
                _userSkill.getRandomUnlockedSignExcluding((_currentScreen.value as Exercise).sign)
            } else {
                _userSkill.getRandomUnlockedSign()
            }

        val filteredExercises = filterExercises()

        return instantiateExerciseFragment(filteredExercises.random(), sign)
    }

    private fun extractSign(exerciseFragment: Fragment) = (exerciseFragment as Exercise).sign
}

class LessonViewModelFactory(
    var currentScreenChanged: ((lessonScreen: Fragment) -> Unit)? = null
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LessonViewModel(currentScreenChanged) as T
    }
}