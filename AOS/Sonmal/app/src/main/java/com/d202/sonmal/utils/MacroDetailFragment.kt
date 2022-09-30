package com.d202.sonmal.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    }

    // 인터페이스
    interface OnButtonClickListener {
        fun onButton1Clicked(item: MacroDto) // 삭제
        fun onButton2Clicked() // 확인
        fun onButton3Clicked() // 이동
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}