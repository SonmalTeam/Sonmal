package com.d202.sonmal.ui.call.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d202.sonmal.model.dto.Chat
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.ui.voice.RecordingDialogFragment
import com.d202.sonmal.utils.MainSharedPreference
import com.d202.sonmal.utils.showToast
import com.google.common.flogger.backend.LogData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.webrtc.EglRenderer.FrameListener
import org.webrtc.SurfaceViewRenderer
import java.util.*

private const val RECOGNIZE_INTERVAL = 100L
private const val TAG ="CallViewModel"
class CallViewModel: ViewModel(), TextToSpeech.OnInitListener{
    // WebRTC -> TFLite
    private val _surfaceViewRenderer = MutableLiveData<SurfaceViewRenderer>()
    val surfaceViewRenderer: LiveData<SurfaceViewRenderer>
        get() = _surfaceViewRenderer

    private val _bitmap = MutableLiveData<Bitmap?>()
    val bitmap : LiveData<Bitmap?>
        get() = _bitmap
    fun setSurfaceViewRenderer(surfaceViewRenderer: SurfaceViewRenderer) {
        _surfaceViewRenderer.value = surfaceViewRenderer
    }
    fun getRemoteFrames(){
        viewModelScope.launch(Dispatchers.IO){
            while (true){
                delay(RECOGNIZE_INTERVAL)
                surfaceViewRenderer.value?.addFrameListener(object : FrameListener{
                    override fun onFrame(p0: Bitmap?) {
                        if(p0 != null) {
                            _bitmap.postValue(p0)
                        }
                    }
                }, 1.0f)
            }
        }
    }

    // SignLanguage
    private val _translateText = MutableLiveData<String>("")
    val translateText: LiveData<String>
        get() = _translateText
    fun setTranslateText(text: String){
        _translateText.postValue(text)
    }

    // Firebase Chat
    private var _db: DatabaseReference = Firebase.database.getReference("chat-message")
    private val _chatList = MutableLiveData<MutableList<Chat>>(mutableListOf())
    val chatList : LiveData<MutableList<Chat>>
        get() = _chatList

    fun initFirebaseDatabase(userName: String){
        _db.removeValue()
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chattingItem = snapshot.getValue(Chat::class.java)!!
                chattingItem.firebaseKey = snapshot.key ?: ""
                if(chattingItem.name != userName) {
                    _chatList.value!!.add(chattingItem)
                    _chatList.postValue(_chatList.value)
                    speakOut(chattingItem.message)
                }
            }
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onChildRemoved(snapshot: DataSnapshot) {
                _chatList.value!!.removeIf {
                    it.firebaseKey == snapshot.key
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        }
        _db.addChildEventListener(childEventListener)
    }

    fun sendMessage(message: String, userName: String){
        if(message.isNotEmpty()){
            viewModelScope.launch(Dispatchers.IO) {
                _db.push().setValue(Chat("", userName, message))
            }
        }
    }

    // TTS
    private lateinit var tts: TextToSpeech
    fun initTTS(context: Context){
        tts = TextToSpeech(context, this)
    }

    override fun onInit(p0: Int) {
        if(p0 == TextToSpeech.SUCCESS) {
            tts.language = Locale.KOREAN
        }
    }
    private fun speakOut(text: String){
        tts.setPitch(1f)
        tts.setSpeechRate(1f)
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "id1")
    }

    // STT
    private lateinit var translateInterface : TranslateInterface
    private lateinit var speechRecognizer: SpeechRecognizer
    private var FLAG_STT = false
    interface TranslateInterface {
        fun getResult(result: String)
    }
    private fun setInterface(translateInterface : TranslateInterface) {
        this.translateInterface = translateInterface
    }
    fun startSTT(context: Context, userName: String) {
        FLAG_STT = true
        setInterface(object : TranslateInterface{
            override fun getResult(result: String) {
                Log.d(TAG, "getResult: ${result}")
            }
        })
        
        val ii = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra("android.speech.extra.DICTATION_MODE", true)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.apply {
            setRecognitionListener(recognitionListener(context, userName))
            startListening(speechRecognizerIntent)
        }

    }

    fun stopSTT(){
        FLAG_STT = false
    }

    private val _sttStatus= MutableLiveData<String>("")
    val sttStatus: LiveData<String>
        get() = _sttStatus

    private val _sttResult = MutableLiveData<String>()
    val sttResult: LiveData<String>
        get() = _sttResult

    private fun recognitionListener(context: Context, userName: String) = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "onReadyForSpeech: ")
            _sttStatus.postValue("onReadyForSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Log.d(TAG, "onBufferReceived: ")}

        override fun onPartialResults(partialResults: Bundle?) {
            Log.d(TAG, "onPartialResults: ")}

        override fun onEvent(eventType: Int, params: Bundle?) {
            Log.d(TAG, "onEvent: ")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech: ")
            _sttStatus.postValue("onBeginningOfSpeech")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech: ")
            _sttStatus.postValue("onEndOfSpeech")
            if(FLAG_STT)
                startSTT(context, userName)
        }


        override fun onError(error: Int) {
            if(FLAG_STT)
                startSTT(context, userName)
            when (error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(
                    context,
                    "퍼미션 없음",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onResults(results: Bundle) {
            if(FLAG_STT)
                startSTT(context, userName)
            translateInterface.getResult(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0])
            val result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
            sendMessage(result, userName)
            if(result.isNotEmpty()) {
                _sttResult.postValue(result)
            }
        }
    }


}