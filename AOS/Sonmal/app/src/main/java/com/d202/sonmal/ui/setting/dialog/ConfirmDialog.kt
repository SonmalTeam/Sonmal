package com.d202.sonmal.ui.setting.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.d202.sonmal.databinding.DialogConfirmBinding
import com.d202.sonmal.utils.getDeviceSize

private const val TAG = "ConfirmDialog"
class ConfirmDialog: DialogFragment() {
    private lateinit var binding: DialogConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogConfirmBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnRemove.setOnClickListener {
                buttonClickListener.onButton1Clicked()
                dismiss()
            }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = getDeviceSize(requireActivity()).x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
    // 인터페이스
    interface OnButtonClickListener {
        fun onButton1Clicked() // 삭제
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}