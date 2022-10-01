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
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.FragmentMacroAddBinding
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel
import java.io.File
import java.text.SimpleDateFormat


private val TAG = "MacroAddFragment"
class MacroAddFragment: Fragment() {
    private lateinit var binding: FragmentMacroAddBinding
    private val macroViewmodel: MacroViewModel by viewModels()
    private var selectedCategoty: Int = 0

    //emoji 입력
    private var emoji = "Emoji"

    // 권한
    private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    private val CAMERA_PERMISSION_FLAG = 100
    private val STORAGE_PERMISSION = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val STORAGE_PERMISSION_FLAG = 200

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

        if(checkPermission(CAMERA_PERMISSION, CAMERA_PERMISSION_FLAG)){
//            checkPermission(STORAGE_PERMISSION, STORAGE_PERMISSION_FLAG)
        }

        binding.btnRecord.setOnClickListener {
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
                }                false -> {
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
                binding.tvEmoji.text ==  "" ||
                    selectedCategoty == 0) {
                Toast.makeText(requireContext(), "제목, 내용, 아이콘, 카테고리 입력 필요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "macro add start")


            // 필수
            var title = binding.etTitle.text.toString()
            var content = binding.etContent.text.toString()
            var category = selectedCategoty// todo categrySeq 적용
            var emoji = this.emoji // todo emoji만 입력

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
            imgCategory1.setOnClickListener {
                selectedCategoty = 1
            }
            imgCategory2.setOnClickListener {
                selectedCategoty = 2
            }
            imgCategory3.setOnClickListener {
                selectedCategoty = 4
            }
            imgCategory4.setOnClickListener {
                selectedCategoty = 3
            }
            imgCategory5.setOnClickListener {
                selectedCategoty = 5
            }
            imgCategory6.setOnClickListener {
                selectedCategoty = 6
            }
        }
    }

    private fun initObserver() {
        macroViewmodel.macroAddCallback.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "등록 완료", Toast.LENGTH_SHORT).show()
            if(videoFileSave != null) {
                videoFileSave!!.delete()
            }
            findNavController().navigateUp()

        }
        macroViewmodel.refreshExpire.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            ApplicationClass.mainPref.loginPlatform = 0
            findNavController().navigate(MacroAddFragmentDirections.actionMacroAddFragmentToLoginFragment())
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
                        Toast.makeText(requireContext(), "카메라 권한을 승인해야지만 앱을 사용 할 수 있습니다.", Toast.LENGTH_LONG).show()
                    }else{
//                        checkPermission(STORAGE_PERMISSION, STORAGE_PERMISSION_FLAG)
                    }
                }
            }
//            STORAGE_PERMISSION_FLAG -> {
//                for(grant in grantResults) {
//                    if(grant != PackageManager.PERMISSION_GRANTED){
//                        Toast.makeText(requireContext(), "저장소 권한을 승인해야지만 앱을 사용 할 수 있습니다.", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
        }
    }

    private fun showdialog(){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Title")

        val input = EditText(requireContext())
        input.setHint("Enter Text")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            var textE = input.text.toString()
            binding.tvEmoji.text = textE
            this.emoji = textE
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    //cache 이용 테스트
    private fun createCacheFile() {
        var filename = "a"
        val cacheFile = File(requireContext().cacheDir,filename)
//        cacheFile.delete() // 특정 파일 삭제
//        requireContext().deleteFile(filename) // 캐시에서 해당 이름의 파일 삭제제
   }

    private fun deleteCacheFile() {
        var filename = "a"
    }

}