package com.d202.sonmal.ui.macro

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.d202.sonmal.adapter.MacroAdapter
import com.d202.sonmal.databinding.FragmentMacroCafeBinding
import com.d202.sonmal.databinding.FragmentMacroChoiceBinding
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.ui.MainActivity
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel
import java.util.*

private val TAG = "MacroCafeFragment"
class MacroCafeFragment: Fragment() {

    private lateinit var binding: FragmentMacroCafeBinding
    private val macroViewModel: MacroViewModel by viewModels()
    private lateinit var macroList: MutableList<MacroDto>
    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMacroCafeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObseve()

        //todo 진입 루트에 따라 다른 매크로 리스트 띄우기
        val userSeq = 1
        val category = 1
        macroViewModel.getMacroList(userSeq, category)

    }

    private fun initObseve() {
        macroViewModel.macroList.observe(viewLifecycleOwner) {
            Log.d(TAG, "macrolist in viewmoel $it")
            if(it != null) {
                this.macroList = it
            }

            initAdapter()
            initTTS()
        }
    }

    private fun initAdapter() {
        val macroAdapter = MacroAdapter(macroList)
        binding.rcyMacro.layoutManager = LinearLayoutManager(context)
        binding.rcyMacro.adapter = macroAdapter

        macroAdapter.setSpeakClickListener(object: MacroAdapter.SpeakItemClickListener{
            override fun onClick(view: View, position: Int, item: MacroDto) {
                Toast.makeText(requireContext(), "${item.content}", Toast.LENGTH_SHORT).show()
                speak(item.content)
            }
        })

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
        if(tts != null) {
            tts.stop()
            tts.shutdown()
        }
    }
}