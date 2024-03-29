package com.d202.sonmal.ui.main

import android.Manifest.permission.*
import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Context.ROLE_SERVICE
import android.content.Context.TELECOM_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.common.TFLITE_PATH
import com.d202.sonmal.databinding.FragmentMainBinding
import com.d202.sonmal.utils.showAlertDialog
import com.d202.sonmal.utils.showToast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

private const val TAG = "MainFragment"
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var callback1: OnBackPressedCallback

    private lateinit var defualtAppIntent: Intent
    private lateinit var telecomManager: TelecomManager
    private lateinit var roleManager: RoleManager
    private lateinit var intent: Intent
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback1 = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        telecomManager = requireContext().getSystemService(TELECOM_SERVICE) as TelecomManager
        roleManager = requireContext().getSystemService(ROLE_SERVICE) as RoleManager
        intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        defualtAppIntent = intent

        initView()
        ApplicationClass.interpreter = Interpreter(loadModelFile(requireActivity(), TFLITE_PATH)!!)

    }


    var requireDefualtDialerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val thisDefaultDialer = requireContext().packageName == telecomManager.defaultDialerPackage
            if(!thisDefaultDialer){
                Toast.makeText(requireContext(),"기본 전화 앱 설정은 필수입니다.", Toast.LENGTH_SHORT).show()
            }else{
                checkCallPermission()
            }
        }
    private fun initView() {
        binding.apply {
            btnMacro.setOnClickListener { // btn m
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingFragment())
            }
            btnCall.setOnClickListener {
                requireActivity().showAlertDialog("알림","베타기능 이용 시 오류가 발생할 수 있습니다.\n사용에 주의해 주세요.", object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        if(requireContext().packageName != telecomManager.defaultDialerPackage){
                            requireDefualtDialerLauncher.launch(defualtAppIntent)
                        }else{
                            checkCallPermission()
                        }
                    }
                })

            }
            btnSignLang.setOnClickListener {
                checkPermission(0)
            }
            btnVoiceTranslate.setOnClickListener {
                checkPermission(1)
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


    private fun checkPermission(flag : Int){
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                when(flag) {
                    0 -> { // 수어 통역
                        findNavController().navigate(MainFragmentDirections.actionMainFragmentToSignLangFragment())
                    }
                    1 -> { // 음성 자막
                        findNavController().navigate(MainFragmentDirections.actionMainFragmentToVoiceFragment())
                    }
                    2 -> { // 매크로
                        findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingFragment())
                    }
                    else -> {
                    }
                }
            }
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                requireContext().showToast("권한을 허용해야 이용이 가능합니다.")
            }
        }
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(CAMERA, RECORD_AUDIO, MODIFY_AUDIO_SETTINGS)
            .check()
    }

    private fun checkCallPermission(){
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToDialFragment())

            }
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                requireContext().showToast("권한을 허용해야 이용이 가능합니다.")
            }
        }
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(CAMERA, RECORD_AUDIO, MODIFY_AUDIO_SETTINGS, READ_CONTACTS, READ_PHONE_NUMBERS, READ_CALL_LOG, PROCESS_OUTGOING_CALLS, CALL_PHONE, ANSWER_PHONE_CALLS)
            .check()
    }
}