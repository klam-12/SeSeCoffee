package com.example.sesecoffee.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.adapters.CustomerAdapter
import com.example.sesecoffee.databinding.FragmentAdminChatBinding
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.MessageViewModel
import kotlinx.coroutines.flow.collectLatest

class AdminChatFragment : Fragment() {
    private var _binding : FragmentAdminChatBinding?=null
    private val binding get()= _binding!!
    lateinit var customerAdapter:CustomerAdapter
    lateinit var messageViewModel: MessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminChatBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageViewModel = (activity as AdminMainActivity).messageViewModel
        setUpRecyclerView()

        lifecycleScope.launchWhenResumed {
            messageViewModel.fetchAMessageForEachUser(customerAdapter)
            messageViewModel.message.collectLatest {
                when(it){
                    is Resource.Loading -> {
//                        showLoading()
                        Log.i("load","loainggg")
                    }
                    is Resource.Success -> {

                        customerAdapter.differ.submitList(it.data)
//                        hideLoading()
                    }
                    is Resource.Error -> {
//                        hideLoading()
                        Log.i("error","errrorrr")

                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun setUpRecyclerView(){
        customerAdapter = CustomerAdapter(requireContext())

        binding.chatRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
            adapter = customerAdapter
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}