package com.d202.sonmal.ui.voice

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.d202.sonmal.databinding.FragmentUploadingDialogBinding
import java.util.*
import kotlin.concurrent.thread

class UploadingDialogFragment: DialogFragment() {
    private var _binding: FragmentUploadingDialogBinding? = null
    private val binding get() = _binding!!
    private var flag = 0
    private lateinit var translateInterface : TranslateInterface

    public interface TranslateInterface {
        fun getResult(result: String)
    }

    public fun setInterface(translateInterface : TranslateInterface) {
        this.translateInterface = translateInterface
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setOnKeyListener { dialogInterface, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                if(flag == 1) {
                    dismiss()
                }
                else {
                    Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다!",Toast.LENGTH_SHORT).show()
                    thread(start=true) {
                        flag = 1
                        Thread.sleep(2500)
                        flag = 0
                    }
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding = FragmentUploadingDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (_: java.lang.IllegalStateException) {

        }
    }
}