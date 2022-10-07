package com.d202.sonmal.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.DialogFragment
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.DialogVideoBinding

private const val TAG = "VideoDialogFragment"
class VideoDialogFragment(var videoId: Int): DialogFragment() {
    private lateinit var binding: DialogVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogVideoBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.vv)

        var video = "https://d202.kro.kr/api/sign/macro/video/$videoId"
        var uriVideo = Uri.parse(video)
        binding.vv.apply {
            setMediaController(mediaController)
            setVideoURI(uriVideo, mapOf("JWT-AUTHENTICATION" to ApplicationClass.mainPref.refreshToken))
            requestFocus()
            start()
        }

        binding.btn1.setOnClickListener {
            dismiss()
        }


    }
}