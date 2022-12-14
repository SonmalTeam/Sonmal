package com.d202.sonmal.ui.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.FragmentSettingBinding
import com.d202.sonmal.ui.setting.dialog.ConfirmDialog
import com.d202.sonmal.ui.sign.viewmodel.SignViewModel
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback

private const val TAG = "SettingFragment"
class SettingFragment: Fragment(){

    private lateinit var binding: FragmentSettingBinding
    private val signViewModel: SignViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserve()

        binding.apply {
            tvMacroEdit.setOnClickListener {
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToMacroChoiceFragment())
            }
            tvUnregister.setOnClickListener {
                val confirmDialog = ConfirmDialog()
                confirmDialog.setButtonClickListener(object: ConfirmDialog.OnButtonClickListener{
                    override fun onButton1Clicked() {
                        if(ApplicationClass.mainPref.loginPlatform == 1) {
                            kakaoUnlink()
                        } else if(ApplicationClass.mainPref.loginPlatform == 2) {
                            naverUnlink()
                        }
                    }

                })
                confirmDialog.show(parentFragmentManager, "ConfirmDialog")

            }
            tvAppInfo.setOnClickListener {
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToAppInfoFragment())
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }

    private fun initObserve() {
        signViewModel.unregisterCallBack.observe(viewLifecycleOwner) {
            if(it == true) {
                Toast.makeText(requireContext(), "?????? ?????? ??????", Toast.LENGTH_LONG).show()
                ApplicationClass.mainPref.apply {
                    token = null
                    refreshToken = null
                    loginPlatform = 0
                }
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
            } else {
                Toast.makeText(requireContext(), "?????? ?????? ?????? ??????", Toast.LENGTH_LONG).show()
                ApplicationClass.mainPref.apply {
                    token = null
                    refreshToken = null
                    loginPlatform = 0
                }
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
            }
        }

        signViewModel.refreshExpire.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "?????? ?????????????????????.", Toast.LENGTH_SHORT).show()
            ApplicationClass.mainPref.loginPlatform = 0
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
        }
    }

    private fun kakaoLogout(){ // ????????? ????????????
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.d(TAG, "???????????? ??????. SDK?????? ?????? ?????????: ${error}")
            }
            else {
                Log.d(TAG, "???????????? ??????. SDK?????? ?????? ?????????")
            }
        }
    }

    private fun naverLogout(){
        NaverIdLoginSDK.logout()
        Toast.makeText(requireContext(), "????????? ????????? ???????????? ??????!", Toast.LENGTH_SHORT).show()
    }

    private fun kakaoUnlink(){ // ????????? ????????????
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Toast.makeText(requireContext(), "?????? ?????? ??????", Toast.LENGTH_LONG).show()
                ApplicationClass.mainPref.apply {
                    token = null
                    refreshToken = null
                    loginPlatform = 0
                }
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
            }
            else {
            }
            Toast.makeText(requireContext(), "????????? ?????? ?????? ??????", Toast.LENGTH_LONG).show()
            signViewModel.unregister()
        }
    }

    private fun naverUnlink() {
        NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
            override fun onSuccess() {
                Toast.makeText(requireContext(), "????????? ?????? ?????? ??????", Toast.LENGTH_LONG).show()
                signViewModel.unregister()

            }
            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(requireContext(), "?????? ?????? ??????", Toast.LENGTH_LONG).show()
                ApplicationClass.mainPref.apply {
                    token = null
                    refreshToken = null
                    loginPlatform = 0
                }
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
            }
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(requireContext(), "?????? ?????? ??????", Toast.LENGTH_LONG).show()
                ApplicationClass.mainPref.apply {
                    token = null
                    refreshToken = null
                    loginPlatform = 0
                }
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
                onFailure(errorCode, message)
            }
        })
    }
}