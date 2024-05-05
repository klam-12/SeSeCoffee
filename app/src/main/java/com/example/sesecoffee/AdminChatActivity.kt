package com.example.sesecoffee

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.adapters.MessageApdapter
import com.example.sesecoffee.databinding.FragmentChatBinding
import com.example.sesecoffee.model.Message
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.MessageViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdminChatActivity : AppCompatActivity() {
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var messageApdapter: MessageApdapter
    private  var binding : FragmentChatBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val userId = intent.getStringExtra("userId")
        val userName= intent.getStringExtra("userName")
        val avatar= intent.getStringExtra("avatar")
        binding = DataBindingUtil.setContentView(this,R.layout.fragment_chat)
        messageViewModel = MessageViewModel(application)
        setUpRecyclerView()
        lifecycleScope.launchWhenResumed {
            messageViewModel.fetchAllMessageAUser(userId.toString(),messageApdapter)
            messageViewModel.message.collectLatest {
                when(it){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        messageApdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
//                        Toast.makeText(context,it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        binding?.apply {
            layoutSend.setOnClickListener(){

                val mess= binding!!.inputMessage.text
                if (mess.toString() != null) {
                    val currentDateTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
                    val formattedDateTime = currentDateTime.format(formatter)
                    var newMess = Message(
                        mess.toString(),
                        true,
                        userName.toString(),
                        formattedDateTime,
                        userId,
                        avatar
                    )
                    messageViewModel.addMessage(newMess, userId.toString())
                    messageApdapter.addFirst(newMess)
                    binding!!.inputMessage.setText("")
                }
            }
        }
    }
    private fun setUpRecyclerView(){
        messageApdapter = MessageApdapter(true)

        binding!!.chatRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)
            adapter = messageApdapter
        }
    }

}