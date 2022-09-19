package com.d202.sonmal.ui.macro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.d202.sonmal.databinding.FragmentMacroCafeBinding
import com.d202.sonmal.databinding.FragmentMacroChoiceBinding

class MacroCafeFragment: Fragment() {

    private lateinit var binding: FragmentMacroCafeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMacroCafeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }

}