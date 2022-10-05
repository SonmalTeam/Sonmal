package com.d202.sonmal.ui.voice


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.d202.sonmal.R
import com.d202.sonmal.databinding.BottomSheetMacroLayoutBinding
import com.d202.sonmal.ui.macro.MacroCafeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MacroBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetMacroLayoutBinding

    enum class Category {
        Hospital, Official, Restaurant, Traffic, Etc, Work
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnHospital.setOnClickListener {
                moveTo(Category.Hospital)
            }
            btnTraffic.setOnClickListener {
                moveTo(Category.Traffic)
            }
            btnOffical.setOnClickListener {
                moveTo(Category.Official)
            }
            btnRestaurant.setOnClickListener {
                moveTo(Category.Restaurant)
            }
            btnWork.setOnClickListener {
                moveTo(Category.Work)
            }
            btnEtc.setOnClickListener {
                moveTo(Category.Etc)
            }
        }
    }

    private fun moveTo(where: Category) {
        val bundle = Bundle()
        bundle.putInt("args", where.ordinal + 1)
        
        when(where) {
            Category.Hospital -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Category.Traffic -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Category.Official -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Category.Restaurant -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Category.Work -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Category.Etc -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
        }
    }
}