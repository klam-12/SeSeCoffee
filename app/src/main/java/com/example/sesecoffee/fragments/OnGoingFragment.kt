package com.example.sesecoffee.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.OnGoingAdapter
import com.example.sesecoffee.databinding.FragmentOnGoingBinding
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.example.sesecoffee.MainActivity
import kotlinx.coroutines.flow.collectLatest
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderTrackingViewModel

class OnGoingFragment : Fragment(R.layout.fragment_on_going){

    private var _binding: FragmentOnGoingBinding? = null
    private val binding get() = _binding!!

    private lateinit var onGoingAdapter: OnGoingAdapter
    private lateinit var orderTrackingViewModel: OrderTrackingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnGoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderTrackingViewModel = (activity as MainActivity).orderTrackingViewModel

        setUpRecyclerViewOrders()
        lifecycleScope.launchWhenStarted {
            orderTrackingViewModel.onGoingOrderItems.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        onGoingAdapter.differ.submitList(it.data)
                        hideLoading()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }


    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE

    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerViewOrders() {
        onGoingAdapter = OnGoingAdapter(null);
        binding.onGoingList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = onGoingAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}