package com.d202.sonmal.ui.macro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.d202.sonmal.R
import com.d202.sonmal.databinding.FragmentMacroListBinding

class MacroListFragment : Fragment() {
    private lateinit var binding: FragmentMacroListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMacroListBinding.inflate(inflater, container, false)

        binding.apply {
            ivBack.setOnClickListener {
                parentFragmentManager.beginTransaction().remove(this@MacroListFragment).commit()
            }
        }
        return binding.root
    }
}