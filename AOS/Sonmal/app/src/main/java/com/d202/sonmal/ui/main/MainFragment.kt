package com.d202.sonmal.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.common.TFLITE_PATH
import com.d202.sonmal.databinding.FragmentMainBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        // Interpreter 초기화
        ApplicationClass.interpreter = Interpreter(loadModelFile(requireActivity(), TFLITE_PATH)!!)

    }

    private fun initView() {
        binding.apply {
            btnMacro.setOnClickListener { // btn macro 클릭 시 macro 분류 선택 프래그먼트로 이동
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToMacroChoiceFragment())
            }
            btnCall.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToCallFragment())
            }
//            btnLogin.setOnClickListener {
//                findNavController().navigate(MainFragmentDirections.actionMainFragmentToLoginFragment())
//            }
//            btnVideo.setOnClickListener {
//                findNavController().navigate(MainFragmentDirections.actionMainFragmentToMacroVideoFragment())
//            }
            btnSignLang.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSignLangFragment())
            }
            btnVoiceTranslate.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToVoiceFragment())
            }
        }
    }


    private fun loadModelFile(activity: Activity, path: String): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


}