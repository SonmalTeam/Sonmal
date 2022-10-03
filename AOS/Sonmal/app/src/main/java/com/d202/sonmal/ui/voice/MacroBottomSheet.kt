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

    enum class Place {
        Hospital, Traffic, Official, Restaurant, Work, Etc
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
                moveTo(Place.Hospital)
            }
            btnTraffic.setOnClickListener {
                moveTo(Place.Traffic)
            }
            btnOffical.setOnClickListener {
                moveTo(Place.Official)
            }
            btnRestaurant.setOnClickListener {
                moveTo(Place.Restaurant)
            }
            btnWork.setOnClickListener {
                moveTo(Place.Work)
            }
            btnEtc.setOnClickListener {
                moveTo(Place.Etc)
            }
        }
    }

    private fun moveTo(where: Place) {
        val bundle = Bundle()
        bundle.putInt("args", where.ordinal + 1)
        
        when(where) {
            Place.Hospital -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Place.Traffic -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Place.Official -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Place.Restaurant -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Place.Work -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
            Place.Etc -> {
                val fragment = MacroCafeFragment()
                fragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            }
        }
    }
}