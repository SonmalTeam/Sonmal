package com.d202.sonmal.ui.voice


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.R
import com.d202.sonmal.databinding.BottomSheetMacroLayoutBinding
import com.d202.sonmal.ui.macro.MacroCafeFragment
import com.d202.sonmal.ui.macro.MacroChoiceFragment
import com.d202.sonmal.ui.macro.MacroChoiceFragmentDirections
import com.d202.sonmal.ui.macro.MacroListFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MacroBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetMacroLayoutBinding

    enum class Place {
        Cafe, Hospital, Public, Store, Restaurant, Custom
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
            btnCafe.setOnClickListener {
                moveTo(Place.Cafe)
            }
            btnHospital.setOnClickListener {
                moveTo(Place.Hospital)
            }
            btnPublic.setOnClickListener {
                moveTo(Place.Public)
            }
            btnStore.setOnClickListener {
                moveTo(Place.Store)
            }
            btnRestaurant.setOnClickListener {
                moveTo(Place.Restaurant)
            }
            btnCustom.setOnClickListener {
                moveTo(Place.Custom)
            }
        }
    }

    private fun moveTo(where: Place) {
        when(where) {
            Place.Cafe -> findNavController().navigate(VoiceFragmentDirections.actionMacroBottomSheetToMacroCafeFragment())
            Place.Hospital -> {
                childFragmentManager.beginTransaction().replace(R.id.container, MacroCafeFragment()).commit()
            }
            Place.Public -> {}
            Place.Store -> {}
            Place.Restaurant -> {}
            Place.Custom -> {}
        }
    }
}