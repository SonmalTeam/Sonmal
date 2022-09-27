package com.d202.sonmal.ui.macro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.R
import com.d202.sonmal.databinding.FragmentMacroChoiceBinding

class MacroChoiceFragment: Fragment() {

    private lateinit var binding: FragmentMacroChoiceBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMacroChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) // navcontroller 탐색

        initBtn()

        binding.btnCafe.setOnClickListener{ // cafe macro로 이동
            navController.navigate(R.id.action_macroChoiceFragment_to_macroCafeFragment)
        }

        binding.btnStore.setOnClickListener{ // store macro로 이동
            moveToMacroListFragment()
        }

        binding.btnHospital.setOnClickListener{ // hospital macro로 이동
            moveToMacroListFragment()
        }

    }

    private fun initBtn() {
        binding.apply {
            btnMacroList.setOnClickListener {
                moveToMacroListFragment()
            }

            btnAdd.setOnClickListener {
                findNavController().navigate(MacroChoiceFragmentDirections.actionMacroChoiceFragmentToMacroAddFragment())
            }
        }
    }

    private fun moveToMacroListFragment() {
        findNavController().navigate(MacroChoiceFragmentDirections.actionMacroChoiceFragmentToMacroCafeFragment())
    }

}