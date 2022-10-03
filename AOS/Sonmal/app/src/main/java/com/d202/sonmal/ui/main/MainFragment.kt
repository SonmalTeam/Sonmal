package com.d202.sonmal.ui.main

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.R
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.common.TFLITE_PATH
import com.d202.sonmal.databinding.FragmentMainBinding
import com.d202.sonmal.ui.call.CallFragment
import com.d202.sonmal.utils.showToast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingFragment())
            }
            btnCall.setOnClickListener {
                checkPermission()
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


    private fun checkPermission(){
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                parentFragmentManager.beginTransaction().replace(R.id.frame_main, CallFragment().apply {
                    arguments = bundleOf("PHONE" to "test2")
                }).commit()
//                findNavController().navigate(MainFragmentDirections.actionMainFragmentToDialFragment())
            }
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                requireContext().showToast("권한을 허용해야 이용이 가능합니다.")
            }

        }
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_CONTACTS, READ_PHONE_NUMBERS, READ_CALL_LOG, PROCESS_OUTGOING_CALLS)
            .check()
    }
}