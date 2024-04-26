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

    lateinit var onGoingAdapter: OnGoingAdapter
    private lateinit var orderTrackingViewModel: OrderTrackingViewModel

    var isEmpty = true
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
        orderTrackingViewModel.fetchAllOnGoingOrderItems()

            lifecycleScope.launchWhenStarted {
                orderTrackingViewModel.onGoingOrder.collectLatest {
                    when(it) {
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            if(it.data?.isEmpty() == true) {
                                showNoData()
                            } else {
                                setUpRecyclerViewOrders()
                                showData()
                                onGoingAdapter.differ.submitList(it.data) // Show data in RecyclerView
                            }
                        }

                        is Resource.Error -> {
                            showNoData()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> Unit
                    }
        }
            }
    }


    private fun showData() {
        binding.onGoingList.visibility = View.VISIBLE
        binding.tvNoProduct.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showNoData() {
        binding.tvNoProduct.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.onGoingList.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvNoProduct.visibility = View.GONE
        binding.onGoingList.visibility = View.GONE
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