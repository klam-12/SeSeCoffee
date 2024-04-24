package com.example.sesecoffee.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.AdminRatingAdapter
import com.example.sesecoffee.databinding.FragmentAdminRatingBinding
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderViewModel
import kotlinx.coroutines.flow.collectLatest

class AdminRatingFragment : Fragment(R.layout.fragment_admin_rating) {
    private var _binding : FragmentAdminRatingBinding? = null
    private val binding get() = _binding!!
    lateinit var ratingAdapter: AdminRatingAdapter
    lateinit var orderViewModel: OrderViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminRatingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderViewModel = (activity as AdminMainActivity).orderViewModel
        setUpRecyclerViewRating()
        lifecycleScope.launchWhenResumed {
            orderViewModel.fetchAllOrders()
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        val listData = it.data

                        // filter data with rating != 0
//                        val filteredListData = listData?.filter { it.rating != 0F }

                        ratingAdapter.differ.submitList(it.data)
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
        binding.progressBarRating.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBarRating.visibility = View.VISIBLE
    }

    private fun setUpRecyclerViewRating() {
        ratingAdapter = AdminRatingAdapter()
        binding.rvRatings.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
            adapter = ratingAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}