package com.example.sesecoffee.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.AddRedeemActivity
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.AdminRedeemAdapter
import com.example.sesecoffee.databinding.FragmentAdminRedeemBinding
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.RedeemItemViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AdminRedeemFragment : Fragment(R.layout.fragment_admin_redeem) {
    private var _binding : FragmentAdminRedeemBinding? = null
    private val binding get() = _binding!!

    lateinit var redeemItemViewModel: RedeemItemViewModel
    lateinit var adminRedeemAdapter: AdminRedeemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminRedeemBinding.inflate(inflater,container,false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        redeemItemViewModel = (activity as AdminMainActivity).redeemItemViewModel

        setUpRecyclerViewRedeem()
        lifecycleScope.launchWhenStarted {
            redeemItemViewModel.fetchAllRedeems()
            redeemItemViewModel.redeems.collectLatest {
                when(it){
                    is Resource.Loading -> {
//                        showLoading()
                    }
                    is Resource.Success -> {
                        adminRedeemAdapter.differ.submitList(it.data)
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

        binding.addRedeemBtn.setOnClickListener(){
            val intent = Intent(requireContext(),AddRedeemActivity::class.java);
            startActivity(intent)

        }
    }

    private fun setUpRecyclerViewRedeem() {
        adminRedeemAdapter = AdminRedeemAdapter()
        binding.rvRedeem.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
            adapter = adminRedeemAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}