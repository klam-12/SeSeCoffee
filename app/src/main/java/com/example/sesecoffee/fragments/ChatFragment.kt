package com.example.sesecoffee.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.MainActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.MessageApdapter
import com.example.sesecoffee.databinding.FragmentChatBinding
import com.example.sesecoffee.model.Message
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.MessageViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private var _binding : FragmentChatBinding?=null

    private val binding get()= _binding!!
    lateinit var messageApdapter: MessageApdapter
    lateinit var messageViewModel: MessageViewModel
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageViewModel = (activity as MainActivity).messageViewModel
        setUpRecyclerView()
        lifecycleScope.launchWhenResumed {
            messageViewModel.fetchAllMessageAUser(UserSingleton.instance?.id.toString(),messageApdapter)
            messageViewModel.message.collectLatest {
                when(it){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        messageApdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.layoutSend.setOnClickListener(){
            val mess=binding.inputMessage.text
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
            val formattedDateTime = currentDateTime.format(formatter)
            var newMess= Message(mess.toString(),formattedDateTime, UserSingleton.instance?.avatar)
            messageViewModel.addMessage(newMess, UserSingleton.instance?.id.toString())
            messageApdapter.addFirst(newMess)
            binding.inputMessage.setText("")
        }
    }
    private fun setUpRecyclerView(){
        messageApdapter = MessageApdapter(false)

        binding.chatRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
            adapter = messageApdapter
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            messageViewModel.fetchAllMessageAUser(UserSingleton.instance?.id.toString(),messageApdapter)
            messageViewModel.message.collectLatest {
                when(it){
                    is Resource.Loading -> {
//                        showLoading()
                    }
                    is Resource.Success -> {

                        messageApdapter.differ.submitList(it.data)
//                        hideLoading()
                    }
                    is Resource.Error -> {
//                        hideLoading()
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
       _binding = null
    }

}