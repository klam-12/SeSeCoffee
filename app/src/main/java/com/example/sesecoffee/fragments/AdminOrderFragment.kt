package com.example.sesecoffee.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.AdminOrderAdapter
import com.example.sesecoffee.adapters.AdminRedeemAdapter
import com.example.sesecoffee.databinding.FragmentAdminOrderBinding
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.example.sesecoffee.viewModel.OrderViewModel
import kotlinx.coroutines.flow.collectLatest

class AdminOrderFragment : Fragment(R.layout.fragment_admin_order) {
    private var _binding : FragmentAdminOrderBinding? = null
    private val binding get() = _binding!!
    lateinit var orderViewModel: OrderViewModel
    lateinit var orderAdapter: AdminOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderViewModel = (activity as AdminMainActivity).orderViewModel
        setUpRecyclerViewOrder()

    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            orderViewModel.fetchAllOrders()
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        orderAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }


    private fun hideLoading() {
        binding.progressBarOrder.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBarOrder.visibility = View.VISIBLE
    }

    private fun setUpRecyclerViewOrder() {
        orderAdapter = AdminOrderAdapter()
        binding.rvOrders.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
            adapter = orderAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}