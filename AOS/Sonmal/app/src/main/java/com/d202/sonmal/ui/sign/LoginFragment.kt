package com.d202.sonmal.ui.sign

import android.content.Context
import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.d202.sonmal.R
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.FragmentLoginBinding
import com.d202.sonmal.ui.main.MainFragmentDirections
import com.d202.sonmal.ui.setting.SettingFragmentDirections
import com.d202.sonmal.ui.sign.dialog.PermissionDialog
import com.d202.sonmal.ui.sign.viewmodel.SignViewModel
import com.d202.sonmal.utils.sharedpref.SettingsPreference
import com.d202.sonmal.utils.showToast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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
            Log.d(TAG, "자동로그인 ${ApplicationClass.mainPref.token}")
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
        checkPermission()

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

        //회원 정보가 이미 있는 경우 true 받아 로그인 api 호출 또는 로그인 처리
        //회원 정보가 없는 경우 회원가입 진행 후 true 받아 로그인 api 호출 또는 로그인 처리
        signViewModel.isJoinSucced.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(R.id.action_loginFragment_to_mainFragment)
                signViewModel.refresh()
            }
//            else { // 서버 회원가입 실패 시 탈퇴 처리
//                if(ApplicationClass.mainPref.loginPlatform == 1) {
//                    kakaoUnlink()
//                }
//                else { // 서버 회원가입 실패 시 탈퇴 처리
//                    naverUnlink()
//                }
//            }
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
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(requireContext(), "카카오 계정 로그인 실패", Toast.LENGTH_LONG).show()
            } else if (token != null) {
                //TODO: 최종적으로 카카오로그인 및 유저정보 가져온 결과
                UserApiClient.instance.me { user, error ->
                    //로그인 성공 시 회원가입 api 호출
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

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.d(TAG,"kakaologin back cancle")
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                } else if (token != null) {
                    //로그인 성공 시 회원가입 api 호출
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
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                naverToken = NaverIdLoginSDK.getAccessToken()
//                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
//                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
//                var naverTokenType = NaverIdLoginSDK.getTokenType()
//                var naverState = NaverIdLoginSDK.getState().toString()

                //로그인 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(profileCallback)

                ApplicationClass.mainPref.loginPlatform = 2
                //로그인 성공 시 회원가입 api 호출
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

//    private fun naverUnlink() {
//        NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
//            override fun onSuccess() {
//                //서버에서 토큰 삭제에 성공한 상태입니다.
//                Toast.makeText(requireContext(), "네이버 아이디 토큰삭제 성공!", Toast.LENGTH_SHORT).show()
//                signViewModel.unregister()
//
//            }
//            override fun onFailure(httpStatus: Int, message: String) {
//                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
//                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
//                Log.d(TAG, "naver 탈퇴 errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
//                Log.d(TAG, "naver 탈퇴 errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
//            }
//            override fun onError(errorCode: Int, message: String) {
//                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
//                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
//                onFailure(errorCode, message)
//            }
//        })
//    }

    private fun checkPermission(){
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
            }
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                requireContext().showToast("전화 권한을 허용해야 이용이 가능합니다.")
            }

        }
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
            .check()

    }

    private fun kakaoUnlink() { // 카카오 회원탈퇴
        // 연결 끊기
        Log.d(TAG, "kakaoUnlink 실행")
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.d(TAG, "연결 끊기 실패: ${error}")
            } else {
                Log.d(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
            Toast.makeText(requireContext(), "다시 가입해 주세요.", Toast.LENGTH_LONG).show()
        }
    }

    private fun naverUnlink() {
        NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
            override fun onSuccess() {
                //서버에서 토큰 삭제에 성공한 상태입니다.
                Toast.makeText(requireContext(), "다시 가입해 주세요.", Toast.LENGTH_LONG).show()

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
