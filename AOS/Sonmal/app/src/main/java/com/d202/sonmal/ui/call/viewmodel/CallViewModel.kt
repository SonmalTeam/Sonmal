package com.d202.sonmal.ui.call.viewmodel

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d202.sonmal.model.dto.Chat
import com.d202.sonmal.model.dto.MacroDto
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

private const val RECOGNIZE_INTERVAL = 100L
private const val TAG ="CallViewModel"
class CallViewModel: ViewModel() {
    private var _db: DatabaseReference = Firebase.database.getReference("chat-message")

    private val _macroList = MutableLiveData<List<MacroDto>>()
    val macroList : LiveData<List<MacroDto>>
        get() = _macroList
    fun getMacroList(){

    }

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

    private val _translateText = MutableLiveData<String>("")
    val translateText: LiveData<String>
        get() = _translateText
    fun setTranslateText(text: String){
        _translateText.postValue(text)
    }


    private val _chatList = MutableLiveData<MutableList<Chat>>(mutableListOf())
    val chatList : LiveData<MutableList<Chat>>
        get() = _chatList
    fun initFirebaseDatabase(userName: String){
        val childEventListener = object : ChildEventListener {
            // Firebase 데이터베이스에 새로운 아이템(ChattingItem)이 추가되면 콜백
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chattingItem = snapshot.getValue(Chat::class.java)!!
                chattingItem.firebaseKey = snapshot.key ?: ""
                if(chattingItem.name != userName) {
                    _chatList.value!!.add(chattingItem)
                }
            }

            @RequiresApi(Build.VERSION_CODES.N)
            // 서버에서 기존의 아이템이 삭제될 경우 호출되는 콜백
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

    fun sendMessage(message: String){
        if(message.isNotEmpty()){
            viewModelScope.launch(Dispatchers.IO) {
                _db.push().setValue(Chat("", "test1", message))
            }
        }
    }
}