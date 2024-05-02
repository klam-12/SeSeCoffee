package com.example.sesecoffee.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.Message
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Resource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageViewModel(app:Application):AndroidViewModel(app) {
    private val _message = MutableStateFlow<Resource<List<Message>>>(Resource.Unspecified())
    val message : StateFlow<Resource<List<Message>>> = _message

    private val _originalMessage = MutableStateFlow<Resource<Message>>(Resource.Unspecified())
    val originalMessage = _originalMessage.asStateFlow()

    private val _updateMassage = MutableStateFlow<Resource<Message>>(Resource.Unspecified())
    val updateMessage = _updateMassage.asStateFlow()

   private var messageList : MutableList<Message>? = null
//    private val fbSingleton = FirebaseSingleton.getInstance()
    private lateinit var mDbRef: DatabaseReference
    fun fetchAllMessageAUser()  {
//        viewModelScope.launch { _message.emit(Resource.Loading()) }
//
//        fbSingleton.db.collection("message")
//            .get()
//            .addOnSuccessListener {
//                    result ->
//                messageList = result.toObjects(Message::class.java)
//                viewModelScope.launch {
//                    _message.emit(Resource.Success(messageList))
//                }
//                Log.i("hello","asdf")
//
//            }.addOnFailureListener() {
//                Toast.makeText(
//                    getApplication(),
//                    "Errors happen when loading the database",
//                    Toast.LENGTH_LONG
//                ).show()
//                viewModelScope.launch {
//                    _message.emit(Resource.Error(it.message.toString()))
//                }
//
//            }
    }
    fun addMessage(message: Message) {
        mDbRef = FirebaseDatabase.getInstance("https://sese-coffee-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("chat")

        viewModelScope.launch { _message.emit(Resource.Loading()) }
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)
        mDbRef.child(UserSingleton.instance?.id.toString()).child(formattedDateTime.toString()).setValue(message)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _message.emit(Resource.Success(messageList))
                    Log.i("dung","hehe")
                }
            }
            .addOnFailureListener() {
                viewModelScope.launch {
                    _message.emit(Resource.Error(it.message.toString()))
                    Log.i("sai","hehe")

                }
            }

    }
}