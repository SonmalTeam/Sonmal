package com.android.signlanguage.ui.lesson.exercises.sign_letter

import android.content.res.ColorStateList
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import com.android.signlanguage.R
import com.android.signlanguage.databinding.FragmentSignLetterExerciseBinding
import com.android.signlanguage.ViewModelInitListener
import com.android.signlanguage.ui.lesson.Exercise
import com.android.signlanguage.ui.lesson.ExerciseRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignLetterExerciseFragment : Fragment(), ViewModelInitListener, Exercise {

    companion object : ExerciseRules {
        private const val TAG = "SignLetterExerciseFragment"

        override val unlockedSignsRequired: Int = 4

        private const val SIGN_BUNDLE = "sign"

        fun newInstance(sign: Char): SignLetterExerciseFragment {
            val args = Bundle()
            args.putChar(SIGN_BUNDLE, sign)
            val fragment = SignLetterExerciseFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var _viewModel: SignLetterExerciseViewModel
    override var viewModelInitialized: ((viewModel: ViewModel) -> Unit)? = null

    override val sign: Char
        get() = requireArguments().getChar(SIGN_BUNDLE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignLetterExerciseBinding.inflate(inflater, container, false)

        val factory = SignLetterExerciseViewModelFactory(sign)
        _viewModel = ViewModelProvider(this, factory).get(SignLetterExerciseViewModel::class.java)
        viewModelInitialized?.invoke(_viewModel)

        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        _viewModel.showAnswerResults = { ra, a ->
            val buttons = listOf(binding.answer1, binding.answer2, binding.answer3, binding.answer4)

            fun markAs(rightAnswer: Boolean, button: Button) {
                button.backgroundTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            if (rightAnswer) R.color.green else R.color.red,
                            null
                        )
                    )
            }

            GlobalScope.launch(Dispatchers.Main) {

                for ((index, button) in buttons.withIndex()) {
                    if (index == a) {
                        button.backgroundTintList =
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.purple,
                                    null
                                )
                            )
                    }
                }

                delay(500)

                for ((index, button) in buttons.withIndex()) {
                    if (index == ra) {
                        if (ra != a) {
                            delay(500)
                            markAs(true, button)
                        } else
                            markAs(true, button)
                    } else if (index == a) {
                        markAs(false, button)
                    }
                }

                delay(750)

                _viewModel.finish(ra == a)
            }
        }

        return binding.root
    }
}