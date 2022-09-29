package com.d202.sonmal.ui.call.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d202.sonmal.model.dto.Chat
import com.d202.sonmal.model.dto.MacroDto
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
}