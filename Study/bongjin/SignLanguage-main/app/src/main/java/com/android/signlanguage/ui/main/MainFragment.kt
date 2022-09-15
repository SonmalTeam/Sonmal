package com.android.signlanguage.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.signlanguage.R
import com.android.signlanguage.databinding.FragmentMainBinding
import com.android.signlanguage.model.skill.UserSkill

class MainFragment : Fragment() {

    companion object {
        private const val TAG = "MainFragment"

        fun newInstance(): MainFragment {
            val args = Bundle()

            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var _viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)

        val factory = MainViewModelFactory(UserSkill.getInstance(requireContext()))
        _viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        _viewModel.lessonStarted = {
            findNavController().navigate(R.id.action_mainFragment_to_lessonFragment)
        }

        _viewModel.progressReset = {
            UserSkill.save(requireContext())
            Toast.makeText(context, getString(R.string.progress_reset_message), Toast.LENGTH_SHORT).show()
        }

        UserSkill.useSavedVersion(requireContext())
        _viewModel.update()

        return binding.root
    }
}