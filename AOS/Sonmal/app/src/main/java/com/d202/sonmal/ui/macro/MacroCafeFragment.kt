package com.d202.sonmal.ui.macro

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.d202.sonmal.R
import com.d202.sonmal.adapter.MacroAdapter
import com.d202.sonmal.adapter.MacroPagingAdapter
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.FragmentMacroCafeBinding
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel
import com.d202.sonmal.ui.setting.SettingFragmentDirections
import com.d202.sonmal.utils.MacroDetailFragment
import com.d202.sonmal.utils.VideoDialogFragment
import java.util.*

private val TAG = "MacroCafeFragment"
class MacroCafeFragment: Fragment() {

    private lateinit var binding: FragmentMacroCafeBinding
    private val macroViewModel: MacroViewModel by viewModels()
    private lateinit var macroList: MutableList<MacroDto>
    private lateinit var tts: TextToSpeech
    private lateinit var pagingAdapter: MacroPagingAdapter
    private val args by navArgs<MacroCafeFragmentArgs>()
    private var categorySeq = 0
    private var result = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMacroCafeBinding.inflate(inflater, container, false)
        initObseve()
        initView()
        initTTS()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo 진입 루트에 따라 다른 매크로 리스트 띄우기
        result = arguments?.getInt("args")!!
        categorySeq = if(result > 0) {
            result
        } else {
            args.category
        }
        Log.d(TAG, "onCreateView: $categorySeq")
        macroViewModel.getPagingMacroListValue(categorySeq)


        binding.apply {
            ivBack.setOnClickListener {
                if(result == 0) {
                    findNavController().navigateUp()
                }
                else {
                    parentFragmentManager.beginTransaction().remove(this@MacroCafeFragment).commit()
                }
            }
        }
    }

    private fun initObseve() {
        macroViewModel.macroList.observe(viewLifecycleOwner) {
            if(it != null) {
                this.macroList = it
            }

//            initAdapter()
            initTTS()
        }
        macroViewModel.pagingMacroList.observe(viewLifecycleOwner) {

            pagingAdapter.submitData(this@MacroCafeFragment.lifecycle, it)

        }
        macroViewModel.macroDeleteCallback.observe(viewLifecycleOwner) {
            Log.d(TAG, "deleteMacro success")
            macroViewModel.getPagingMacroListValue(categorySeq)
        }

        macroViewModel.refreshExpire.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            ApplicationClass.mainPref.loginPlatform = 0
            findNavController().navigate(MacroCafeFragmentDirections.actionMacroCafeFragmentToLoginFragment())
        }
    }

    private fun initView() {
        this.pagingAdapter = MacroPagingAdapter()

        binding.rcyMacro.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagingAdapter
        }

        pagingAdapter.apply {
            setSpeakClickListener(object: MacroPagingAdapter.SpeakItemClickListener{
                override fun onClick(view: View, position: Int, item: MacroDto) {
                    Toast.makeText(requireContext(), "${item.content}", Toast.LENGTH_SHORT).show()
                    speak(item.content)
                }
            })

            setVideoClickListener(object: MacroPagingAdapter.VideoItemClickListener{
                override fun onClick(view: View, position: Int, item: MacroDto) {
                    if (item.videoFileId != 0) {
                        val dialog = VideoDialogFragment(item.videoFileId)
                        dialog.show(parentFragmentManager, "VideoDialogFragment")
//                        findNavController().navigate(
//                            MacroCafeFragmentDirections.actionMacroCafeFragmentToMacroVideoFragment(
//                                item.videoFileId
//                            )
//                        )

                    }
                }
            })

            setTitleClickListener(object: MacroPagingAdapter.TitleItemClickListener {
                override fun onClick(view: View, position: Int, item: MacroDto) {

                    val dialog = MacroDetailFragment(item)
                    dialog.show(parentFragmentManager, "MacroDetailFragment")

                    dialog.setButtonClickListener(object: MacroDetailFragment.OnButtonClickListener{
                        override fun onButton1Clicked(item: MacroDto) { // 삭제 확인
                        // 기본 형태의 다이얼로그
                        // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("${item.title}")
                            .setMessage("정말로 삭제하시겠습니까?")
                            .setPositiveButton("확인",
                                DialogInterface.OnClickListener { dialog, id ->
                                    Log.d(TAG,"item 삭제 ${item.seq}")
                                    macroViewModel.deleteMacro(item.seq)
                                })

                        // 다이얼로그를 띄워주기
                        builder.show()
                        }

                        override fun onButton2Clicked() { // 확인 후 종료
                        }

                        override fun onButton3Clicked() { // 카테고리 이동
                        }
                    })

                }
            })

        }
    }

    private fun initTTS() {
        tts = TextToSpeech(requireContext(), TextToSpeech.OnInitListener() {
            @Override
            fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    //사용할 언어를 설정
                    var result = tts.setLanguage(Locale.KOREA);
                    //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(requireContext(), "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        //음성 톤
                        tts.setPitch(0.7f);
                        //읽는 속도
                        tts.setSpeechRate(1.2f);
                    }
                }
            }
        });
    }

    private fun speak(content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            tts.speak(content, TextToSpeech.QUEUE_FLUSH, null, null);
        // API 20
        else
            tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
    }

    override fun onStop() {
        super.onStop()
        try {
            if(tts != null) {
                tts.stop()
                tts.shutdown()
            }
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
        }
    }
}