package com.d202.sonmal.ui.macro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.databinding.FragmentMacroChoiceBinding

class MacroChoiceFragment: Fragment() {

    private lateinit var binding: FragmentMacroChoiceBinding

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

        initBtn()
    }

    private fun initBtn() {
        binding.apply {
            btnMacroAdd.setOnClickListener {
                findNavController().navigate(MacroChoiceFragmentDirections.actionMacroChoiceFragmentToMacroAddFragment())
            }

            btnHospital.setOnClickListener {
                moveToMacroListFragment(1)
            }
            btnOfficial.setOnClickListener {
                moveToMacroListFragment(2)
            }
            btnRestaurant.setOnClickListener {
                moveToMacroListFragment(3)
            }
            btnTraffic.setOnClickListener {
                moveToMacroListFragment(4)
            }
            btnCustom.setOnClickListener {
                moveToMacroListFragment(5)
            }
            btnWork.setOnClickListener {
                moveToMacroListFragment(6)
            }
        }
    }

    private fun moveToMacroListFragment(category: Int) {
        findNavController().navigate(MacroChoiceFragmentDirections.actionMacroChoiceFragmentToMacroCafeFragment(category))
    }
}