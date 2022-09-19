package com.android.signlanguage.ui.lesson.lesson_finished

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.android.signlanguage.ViewModelInitListener
import com.android.signlanguage.databinding.FragmentLessonFinishedBinding

class LessonFinishedFragment : Fragment(), ViewModelInitListener {

    companion object {
        private const val TAG = "LessonFinishedFragment"
    }

    override var viewModelInitialized: ((viewModel: ViewModel) -> Unit)? = null

    private lateinit var _viewModel: LessonFinishedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLessonFinishedBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this).get(LessonFinishedViewModel::class.java)
        viewModelInitialized?.invoke(_viewModel)

        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        return binding.root
    }
}