package com.d202.sonmal.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.R
import com.d202.sonmal.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()


    }

    private fun initView() {
        binding.apply {
            btnMacro.setOnClickListener { // btn macro 클릭 시 macro 분류 선택 프래그먼트로 이동
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToMacroChoiceFragment())
            }
            btnCall.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToCallFragment())
            }
            btnLogin.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToLoginFragment())
            }
            btnVideo.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToMacroVideoFragment())
            }
        }


    }


}