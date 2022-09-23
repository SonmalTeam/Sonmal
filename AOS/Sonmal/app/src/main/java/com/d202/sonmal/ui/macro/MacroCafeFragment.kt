package com.d202.sonmal.ui.macro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.d202.sonmal.adapter.MacroAdapter
import com.d202.sonmal.databinding.FragmentMacroCafeBinding
import com.d202.sonmal.databinding.FragmentMacroChoiceBinding
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel

private val TAG = "MacroCafeFragment"
class MacroCafeFragment: Fragment() {

    private lateinit var binding: FragmentMacroCafeBinding
    private val macroViewModel: MacroViewModel by viewModels()
    private lateinit var macroList: MutableList<MacroDto>

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

        initObseve()

        //todo 진입 루트에 따라 다른 매크로 리스트 띄우기
        val userSeq = 1
        val category = 1
        macroViewModel.getMacroList(userSeq, category)

    }

    private fun initObseve() {
        macroViewModel.macroList.observe(viewLifecycleOwner) {
            Log.d(TAG, "macrolist in viewmoel $it")
            if(it != null) {
                this.macroList = it
            }

            initAdapter()
        }
    }

    private fun initAdapter() {
        val macroAdapter = MacroAdapter(macroList)
        binding.rcyMacro.layoutManager = LinearLayoutManager(context)
        binding.rcyMacro.adapter = macroAdapter

    }
}