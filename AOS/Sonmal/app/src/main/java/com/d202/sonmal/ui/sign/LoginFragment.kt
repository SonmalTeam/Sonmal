package com.d202.sonmal.ui.sign

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.R
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.FragmentLoginBinding
import com.d202.sonmal.ui.sign.dialog.PermissionDialog
import com.d202.sonmal.ui.sign.viewmodel.SignViewModel
import com.d202.sonmal.utils.sharedpref.SettingsPreference
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

private const val TAG = "LoginFragment"
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val signViewModel: SignViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var callback1: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        Log.d(TAG, "${ApplicationClass.mainPref.token} ${ApplicationClass.mainPref.loginPlatform}" )
        if(ApplicationClass.mainPref.token != null && ApplicationClass.mainPref.loginPlatform != 0) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback1 = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view) // navcontroller 탐색

        initView()
        initObserve()

    }

    private fun initView() {
        if(SettingsPreference().getFirstRunCheck()){
            PermissionDialog(requireContext()).show(parentFragmentManager, null)
        }

        binding.apply {
            btnKakaoLogin.setOnClickListener { // 카카오 로그인 및 회원가입
                ApplicationClass.mainPref.loginPlatform = 1
                kakaoLogIn()
            }
            btnNaverLogin.setOnClickListener { // 네이버 로그인 및 회원가입
                ApplicationClass.mainPref.loginPlatform = 2
                naverLogIn()
            }
        }
    }

    private fun initObserve() {

        signViewModel.isJoinSucced.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(R.id.action_loginFragment_to_mainFragment)
                signViewModel.refresh()
            }
        }

        signViewModel.jwtToken.observe(viewLifecycleOwner) {
            ApplicationClass.mainPref.token = it
        }

        signViewModel.refreshtoken.observe(viewLifecycleOwner) {
            ApplicationClass.mainPref.refreshToken = signViewModel.refreshtoken.value
        }

        signViewModel.unregisterCallBack.observe(viewLifecycleOwner) {
            if(it == true) {
                ApplicationClass.mainPref.token = null
                ApplicationClass.mainPref.refreshToken = null
                signViewModel.refresh()
            }

        }
    }

    private fun kakaoLogIn() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(requireContext(), "카카오 계정 로그인 실패", Toast.LENGTH_LONG).show()
            } else if (token != null) {
                UserApiClient.instance.me { user, error ->
                    ApplicationClass.mainPref.loginPlatform = 1
                    signViewModel.joinWithKaKao(token.accessToken)
                }
            }

        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "카카오톡 로그인 실패", Toast.LENGTH_LONG).show()
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                } else if (token != null) {
                    ApplicationClass.mainPref.loginPlatform = 1
                    signViewModel.joinWithKaKao(token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
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

    private fun naverLogIn() {
        var naverToken :String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                val userId = response.profile?.id
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                naverToken = NaverIdLoginSDK.getAccessToken()
                NidOAuthLogin().callProfileApi(profileCallback)
                ApplicationClass.mainPref.loginPlatform = 2
                signViewModel.joinWithNaver(naverToken!!)
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(requireContext(), "네이버 로그인 실패", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "naver login 인증 실패   errorCode: ${errorCode}\n" +
                        "errorDescription: ${errorDescription}")
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(requireContext(), oauthLoginCallback)

    }

    private fun naverLogout(){
        NaverIdLoginSDK.logout()
        Toast.makeText(requireContext(), "네이버 아이디 로그아웃 성공!", Toast.LENGTH_SHORT).show()
    }
}
