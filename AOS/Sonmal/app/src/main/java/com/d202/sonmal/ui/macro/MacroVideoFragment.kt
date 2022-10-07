package com.d202.sonmal.ui.macro

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.FragmentMacroVideoBinding

private const val TAG = "MacroVideoFragment"
class MacroVideoFragment : Fragment() {
    private lateinit var binding: FragmentMacroVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMacroVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: MacroVideoFragmentArgs by navArgs()
        var videoId = args.videoSeq
        var mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.vv)

        Log.d(TAG, "비디오 id $videoId")
        var video = "https://d202.kro.kr/api/sign/macro/video/$videoId"
        var uriVideo = Uri.parse(video)
        binding.vv.apply {
            setMediaController(mediaController)
            setVideoURI(uriVideo, mapOf("JWT-AUTHENTICATION" to ApplicationClass.mainPref.refreshToken))
            requestFocus()
            start()
        }

    }
}