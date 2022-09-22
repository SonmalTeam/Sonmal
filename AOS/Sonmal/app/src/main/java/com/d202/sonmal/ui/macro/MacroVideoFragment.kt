package com.d202.sonmal.ui.macro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
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

        binding.macroWebview.apply {
            webViewClient = WebViewClient()
            loadUrl("https://d202.kro.kr/api/sign/macro/video/7")
            settings.apply {
                javaScriptEnabled = true
                pluginState = WebSettings.PluginState.ON
                useWideViewPort = true
                loadWithOverviewMode = true
            }
        }

    }


}