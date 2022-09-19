package com.android.signlanguage.ui.lesson

import com.android.signlanguage.ui.lesson.exercises.letter_camera.LetterCameraExerciseFragment
import com.android.signlanguage.ui.lesson.exercises.letter_sign.LetterSignExerciseFragment
import com.android.signlanguage.ui.lesson.exercises.sign_letter.SignLetterExerciseFragment

object ExerciseConverter {
    val exercises = arrayListOf(
        LetterSignExerciseFragment::class.java,
        SignLetterExerciseFragment::class.java,
        LetterCameraExerciseFragment::class.java
    )

    fun isExercise(screen: Class<out Any>): Boolean =
        exercises.contains(screen)

    fun extractRules(exercise: Class<out Any>): ExerciseRules {
        return when (exercise) {
            LetterSignExerciseFragment::class.java -> LetterSignExerciseFragment
            SignLetterExerciseFragment::class.java -> SignLetterExerciseFragment
            LetterCameraExerciseFragment::class.java -> LetterCameraExerciseFragment
            else -> throw Exception("Unknown exercise fragment class")
        }
    }
}