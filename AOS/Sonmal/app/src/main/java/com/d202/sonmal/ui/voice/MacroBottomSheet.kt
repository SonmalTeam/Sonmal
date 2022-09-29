package com.d202.sonmal.ui.voice


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.d202.sonmal.R
import com.d202.sonmal.databinding.BottomSheetMacroLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MacroBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetMacroLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetDialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetMacroLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
}