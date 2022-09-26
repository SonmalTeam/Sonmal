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
import com.d202.sonmal.databinding.FragmentMacroAddBinding
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel
import java.io.File
import java.text.SimpleDateFormat


class MacroAddFragment: Fragment() {
    private lateinit var binding: FragmentMacroAddBinding
    private val macroViewmodel: MacroViewModel by viewModels()

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
            checkPermission(STORAGE_PERMISSION, STORAGE_PERMISSION_FLAG)
        }

        binding.btnRecord.setOnClickListener {
            val recordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            val videoFile = File(
                File("${requireActivity().filesDir}/video").apply {
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
            // 필수
            var title = "title이다."
            var content = "글자 수는?"
            var category = "cafe"
            var emoji = this.emoji // todo emoji

            // 선택
            var video: File? = videoFileSave

            macroViewmodel.addMacro(title, content, category, emoji, video)


        }

        binding.btnAddEmoji.setOnClickListener {
            showdialog()
        }
    }

    private fun initObserver() {
        macroViewmodel.macroAddCallback.observe(viewLifecycleOwner) {
            binding.tvEmoji.text = it
            Log.d("emoji", "$it")
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
                        checkPermission(STORAGE_PERMISSION, STORAGE_PERMISSION_FLAG)
                    }
                }
            }
            STORAGE_PERMISSION_FLAG -> {
                for(grant in grantResults) {
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(requireContext(), "저장소 권한을 승인해야지만 앱을 사용 할 수 있습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }
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

}