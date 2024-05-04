package com.example.sesecoffee.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.adapters.CustomerAdapter
import com.example.sesecoffee.adapters.MessageApdapter
import com.example.sesecoffee.model.Message
import com.example.sesecoffee.utils.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun addMessage(message: Message, userId :String) {
        mDbRef = FirebaseDatabase.getInstance("https://sese-coffee-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("chat")

        viewModelScope.launch { _message.emit(Resource.Loading()) }


        mDbRef.child(userId).child(message.dateTime.toString()).setValue(message)
            .addOnSuccessListener {
                    Log.i("dung","dung")
            }
            .addOnFailureListener() {
                    Log.i("sai","sai")
            }
    }
    fun fetchAllMessageAUser(userId: String,messageApdapter: MessageApdapter)  {
        viewModelScope.launch { _message.emit(Resource.Loading()) }
        mDbRef = FirebaseDatabase.getInstance("https://sese-coffee-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("chat")
        val newDBRef = mDbRef.child(userId)
        val messList = mutableListOf<Message>()
        newDBRef.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                messList.clear()
                for(postSnapshot in snapshot.children){
                    val mess = postSnapshot.getValue(Message::class.java)
                    messList.add(mess!!)

                }
                viewModelScope.launch {
                    _message.emit(Resource.Success(messList))
                }
                messageApdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("hsdf","sdjkfj")
            }
        })

    }

    fun fetchAMessageForEachUser(customerAdapter: CustomerAdapter){
//        viewModelScope.launch { _message.emit(Resource.Loading()) }
        val messList = mutableListOf<Message>()
        mDbRef = FirebaseDatabase.getInstance("https://sese-coffee-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("chat")
        mDbRef.addListenerForSingleValueEvent(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
//                    val userId = userSnapshot.key
                    val messagesSnapshot = userSnapshot.children.iterator()
                    if (messagesSnapshot.hasNext()) {
                        val firstMessageSnapshot = messagesSnapshot.next()
                        val firstMessageData = firstMessageSnapshot.getValue(Message::class.java)
                        if (firstMessageData != null) {
                            messList.add(firstMessageData)
                        }
                    }
                }
                viewModelScope.launch { _message.emit(Resource.Success(messList)) }
                Log.i("zau","doc duoc data roi")
                customerAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.i("zau","sai roi")

                // Xử lý khi có lỗi xảy ra
            }
        })
    }
}