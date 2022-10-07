package com.d202.sonmal.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.d202.sonmal.databinding.FragmentMacroDetailDialogBinding
import com.d202.sonmal.model.dto.MacroDto

class MacroDetailFragment(var item: MacroDto): DialogFragment() {
    private lateinit var bindig: FragmentMacroDetailDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindig = FragmentMacroDetailDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return bindig.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindig.apply {
            btnDelete.setOnClickListener {
                buttonClickListener.onButton1Clicked(item)
                dismiss()
            }
            btnConfirm.setOnClickListener {
                buttonClickListener.onButton2Clicked()
                dismiss()
            }
            tvTitleDetail.text = item.title
            tvContentDetail.text = item.content
        }
        resize()
    }

    interface OnButtonClickListener {
        fun onButton1Clicked(item: MacroDto) // 삭제
        fun onButton2Clicked() // 확인
        fun onButton3Clicked() // 이동
    }

    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    private lateinit var buttonClickListener: OnButtonClickListener

    override fun onResume() {
        super.onResume()
        resize()
    }

    private fun resize() {
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y
        params?.width = (deviceWidth * 0.7).toInt()
        params?.height = (deviceHeight * 0.7).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}