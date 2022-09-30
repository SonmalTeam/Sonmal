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
                kakaoUnlink()
                naverUnlink()
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLoginFragment())
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

    private fun kakaoUnlink(){ // 카카오 회원탈퇴
        // 연결 끊기
        Log.d(TAG, "연결 끊기. SDK에서 토큰 삭제 됨" +
                "${signViewModel.refreshtoken.value}")

        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.d(TAG, "연결 끊기 실패: ${error}")
            }
            else {
                Log.d(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨") }
            Toast.makeText(requireContext(), "회원 탈퇴 성공", Toast.LENGTH_LONG).show()
            signViewModel.unregister()
        }
    }


    private fun naverLogout(){
        NaverIdLoginSDK.logout()
        Toast.makeText(requireContext(), "네이버 아이디 로그아웃 성공!", Toast.LENGTH_SHORT).show()
    }

    private fun naverUnlink() {
        NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
            override fun onSuccess() {
                //서버에서 토큰 삭제에 성공한 상태입니다.
                Toast.makeText(requireContext(), "네이버 아이디 토큰삭제 성공!", Toast.LENGTH_SHORT).show()
                signViewModel.unregister()

            }
            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Log.d(TAG, "naver 탈퇴 errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.d(TAG, "naver 탈퇴 errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                onFailure(errorCode, message)
            }
        })
    }
}