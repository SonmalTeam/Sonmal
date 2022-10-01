package com.d202.sonmal.ui.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
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

        binding.apply {
//            tvMacroAdd.setOnClickListener {
//                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToMacroAddFragment())
//            }
            tvMacroEdit.setOnClickListener {
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToMacroChoiceFragment())
            }
            tvUnregister.setOnClickListener {
                ConfirmDialog().show(requireActivity().supportFragmentManager, "ConfirmDialog")
            }
            tvAppInfo.setOnClickListener {
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToAppInfoFragment())
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }

    private fun kakaoLogout(){ // 카카오 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.d(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨: ${error}")
            }
            else {
                Log.d(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    private fun naverLogout(){
        NaverIdLoginSDK.logout()
        Toast.makeText(requireContext(), "네이버 아이디 로그아웃 성공!", Toast.LENGTH_SHORT).show()
    }

}