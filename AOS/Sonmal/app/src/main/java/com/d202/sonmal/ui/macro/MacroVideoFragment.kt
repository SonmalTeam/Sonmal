package com.d202.sonmal.ui.macro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.FragmentMacroVideoBinding
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel

private const val TAG = "MacroVideoFragment"
class MacroVideoFragment : Fragment() {
    private lateinit var binding: FragmentMacroVideoBinding
    private val macroSignViewModel: MacroViewModel by viewModels()

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

        if(videoId != 0) {
                binding.macroWebview.apply {
                    webViewClient = WebViewClient()
                    loadUr(this,"https://d202.kro.kr/api/sign/macro/video/$videoId")
                    settings.apply {
                        javaScriptEnabled = true
                        pluginState = WebSettings.PluginState.ON
                        useWideViewPort = true
                        loadWithOverviewMode = true
                    }
                }
        }
    }

//        macroSignViewModel.getVideo(videoId)
//
//        macroSignViewModel.getVideoCallback.observe(viewLifecycleOwner) {
//            if(macroSignViewModel.getVideoCallback.value != null) {
//                binding.macroWebview.apply {
//                    webViewClient = WebViewClient()
//                    loadUrl(it)
//                    settings.apply {
//                        javaScriptEnabled = true
//                        pluginState = WebSettings.PluginState.ON
//                        useWideViewPort = true
//                        loadWithOverviewMode = true
//                    }
//                }
//            }
//        }

    private fun loadUr(view: WebView, uri: String) {
        var devide: Map<String, String> = mutableMapOf("JWT-AUTHENTICATION" to (ApplicationClass.mainPref.token ?: "l"))
        view.loadUrl(uri, devide)
    }




}