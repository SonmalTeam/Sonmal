package com.android.signlanguage.ui.lesson

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.signlanguage.FinishedListener
import com.android.signlanguage.R
import com.android.signlanguage.ViewModelInitListener
import com.android.signlanguage.databinding.FragmentLessonBinding
import com.android.signlanguage.model.skill.UserSkill
import com.android.signlanguage.ui.detailed_signs.DetailedSignsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LessonFragment : Fragment() {

    companion object {
        private const val TAG = "LessonFragment"
    }

    private lateinit var _viewModel: LessonViewModel
    private lateinit var _binding: FragmentLessonBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        UserSkill.getInstance(requireContext())

        _binding = FragmentLessonBinding.inflate(inflater, container, false)

        val factory = LessonViewModelFactory { onCurrentScreenChanged(it) }
        _viewModel = ViewModelProvider(this, factory).get(LessonViewModel::class.java)

        _binding.lifecycleOwner = this
        _binding.viewModel = _viewModel

        _viewModel.openHelpWindowPrompted = {
            val intent = Intent(context, DetailedSignsActivity::class.java)
            startActivity(intent)
        }

        _viewModel.exitPrompted = {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.lesson_exit_confirmation))
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    // nothing
                }
                .setPositiveButton(getString(R.string.exit)) { _, _ ->
                    _viewModel.finish()
                }
                .show()
        }

        _viewModel.currentScreenChanged = {
            onCurrentScreenChanged(it)
        }

        _viewModel.finished.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it == true) {
                    UserSkill.requireInstance().addPointsForEndedLesson()
                    UserSkill.save(requireContext())
                }
                findNavController().navigateUp()
            }
        }

        GlobalScope.launch {
            _binding.progress.progress = 1
            _binding.progress.progress = 0
        }

        return _binding.root
    }

    override fun onResume() {
        super.onResume()

        _viewModel.progress.value?.let {
            GlobalScope.launch {
                _binding.progress.progress = 1
                _binding.progress.progress = 0
                _binding.progress.progress = it
            }
        }
    }

    private fun onCurrentScreenChanged(screen: Fragment) {
        showScreen(screen)
    }

    private fun showScreen(screenToShow: Fragment) {
        if (screenToShow !is ViewModelInitListener)
            return

        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.exercise_fragment_container, screenToShow)
            .commit()

        screenToShow.viewModelInitialized = { vm ->
            if (vm is FinishedListener) {
                vm.finished.observeForever {
                    if (it != null) {
                        Log.d(TAG, "vmFinished")
                        _viewModel.startNextScreen(!it)
                    }
                }
            } else throw ClassCastException("view model must implement FinishedListener")
        }
    }
}