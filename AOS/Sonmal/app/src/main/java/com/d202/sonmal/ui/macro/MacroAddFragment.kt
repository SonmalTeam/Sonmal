package com.d202.sonmal.ui.macro

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.R
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.FragmentMacroAddBinding
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel
import com.d202.sonmal.utils.UploadingDialogFragment
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.io.File
import java.text.SimpleDateFormat


private val TAG = "MacroAddFragment"
class MacroAddFragment: Fragment() {
    private lateinit var binding: FragmentMacroAddBinding
    private val macroViewmodel: MacroViewModel by viewModels()
    private val uploadingDialogFragment by lazy { UploadingDialogFragment() }
    private var selectedCategoty: Int = 0

    //emoji 입력
    private var emoji = ""

    // 권한
    private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    private val CAMERA_PERMISSION_FLAG = 100

    // 영상 캡쳐
    private val REQUEST_VIDEO_CAPTURE_CODE = 1
    private var videoFileSave: File? = null
    private var videoUri : Uri? = null // video 저장될 Uri

    private var isVideoPlaying = false // VideoView에 영상이 재생되고 있는지 상태를 확인

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMacroAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()

        binding.btnRecord.setOnClickListener {
            if(checkPermission(CAMERA_PERMISSION, CAMERA_PERMISSION_FLAG) == false) {
                Toast.makeText(requireContext(), "영상 등록 시 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                checkPermission()
                return@setOnClickListener
            }
            val recordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            val videoFile = File(
                File("${requireActivity().cacheDir}/video").apply {
                    if(!this.exists()){
                        this.mkdirs()
                    }
                },
                newVideoFileName()
            )
            videoFileSave = videoFile // 서버로 전송할 file
            videoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.d202.sonmal",
                videoFile
            )
            recordVideoIntent.resolveActivity(requireActivity().packageManager)?.also{
                recordVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                startActivityForResult(recordVideoIntent, REQUEST_VIDEO_CAPTURE_CODE)
            }
        }



        binding.btnPlay.setOnClickListener {
            when(isVideoPlaying){
                true -> {
                    isVideoPlaying = false
                    binding.videoView.stopPlayback()
                    binding.btnPlay.text = "Play"
                    binding.videoView.setVideoURI(videoUri)
                }
                false -> {
                isVideoPlaying = true
                binding.btnPlay.text = "Stop"
                binding.videoView.start()
                }
            }
        }

        binding.videoView.setOnCompletionListener {
            binding.btnPlay.text = "Play"
            isVideoPlaying = false
        }

        binding.btnAdd.setOnClickListener {

            if(binding.etTitle.text.toString() == "" ||
                binding.etContent.text.toString() ==  "" ||
                binding.tvEmoji.text.toString() ==  "" ||
                    selectedCategoty == 0) {
                Toast.makeText(requireContext(), "제목, 내용, 아이콘, 카테고리 입력 필요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 필수
            var title = binding.etTitle.text.toString()
            var content = binding.etContent.text.toString()
            var category = selectedCategoty// todo categrySeq 적용
            var emoji = binding.tvEmoji.text.toString() // todo emoji만 입력

            if(videoUri != null) {
                // 선택
                var video: File? = videoFileSave

                macroViewmodel.addMacro(title, content, category.toString(), emoji, video)
            } else if (videoUri == null) {
                macroViewmodel.addMacroNull(title, content, category, emoji)
            }

        }

        binding.btnAddEmoji.setOnClickListener {
            showdialog()
        }

        binding.apply {
            imgHospital.setOnClickListener {
                selectedCategoty = 1
                categoryChange(selectedCategoty)
            }
            imgOfficial.setOnClickListener {
                selectedCategoty = 2
                categoryChange(selectedCategoty)
            }
            imgRestaurant.setOnClickListener {
                selectedCategoty = 3
                categoryChange(selectedCategoty)
            }
            imgTraffic.setOnClickListener {
                selectedCategoty = 4
                categoryChange(selectedCategoty)
            }
            imgCustom.setOnClickListener {
                selectedCategoty = 5
                categoryChange(selectedCategoty)
            }
            imgWork.setOnClickListener {
                selectedCategoty = 6
                categoryChange(selectedCategoty)
            }
        }
    }

    private fun categoryChange(seq: Int) {
        var categories = mutableListOf<TextView>(
            binding.imgHospital, binding.imgOfficial,
            binding.imgRestaurant, binding.imgTraffic,
            binding.imgCustom, binding.imgWork
        )

        var selected = seq-1
        for(i in 0..5) {
            if(i == selected) {
                categories.get(i).setBackgroundResource(R.drawable.background_category_add)
            } else {
                categories.get(i).setBackgroundResource(0)
            }
        }
    }

    private fun initObserver() {
        macroViewmodel.macroAddCallback.observe(viewLifecycleOwner) {
            if(it == 200) {
                Toast.makeText(requireContext(), "등록 완료", Toast.LENGTH_SHORT).show()
                if(videoFileSave != null) {
                    videoFileSave!!.delete()
                }
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "등록 실패", Toast.LENGTH_SHORT).show()
            }


        }
        macroViewmodel.refreshExpire.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            ApplicationClass.mainPref.loginPlatform = 0
            findNavController().navigate(MacroAddFragmentDirections.actionMacroAddFragmentToLoginFragment())
        }
        macroViewmodel.flag.observe(viewLifecycleOwner) {
            when(it) {
                true -> showUploading()
                false -> hideUploading()
            }
        }
    }


    private fun newVideoFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "${filename}.mp4"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == AppCompatActivity.RESULT_OK){
            when(requestCode){
                REQUEST_VIDEO_CAPTURE_CODE -> {
                    binding.videoView.setVideoURI(videoUri)
                    binding.videoView.requestFocus()
                    binding.btnPlay.isEnabled = true
                }
            }
        }}


    private fun checkPermission(permissions : Array<out String>, flag : Int):Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(requireActivity(), permissions, flag)
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_PERMISSION_FLAG -> {
                for(grant in grantResults) {
                    if(grant != PackageManager.PERMISSION_GRANTED){
                    }else{
                    }
                }
            }
        }
    }

    private fun showdialog(){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("제목을 나타내는 이모티콘 등록")
        val input = EditText(requireContext())
        input.setHint("이모티콘 입력")
        builder.setView(input)

        builder.setPositiveButton("등록", DialogInterface.OnClickListener { dialog, which ->
            var textE = input.text.toString()
            binding.tvEmoji.setText(textE)
            this.emoji = textE
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    fun showUploading() {
        if(!uploadingDialogFragment.isAdded) {
            uploadingDialogFragment.isCancelable = false
            uploadingDialogFragment.show(childFragmentManager, "loader")
        }
    }

    fun hideUploading() {
        if(uploadingDialogFragment.isAdded) {
            uploadingDialogFragment.dismissAllowingStateLoss()
        }
    }

    private fun checkPermission(){
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {

            }
            override fun onPermissionDenied(deniedPermissions: List<String>) {
            }
        }
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한 : 카메라]")
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }
}