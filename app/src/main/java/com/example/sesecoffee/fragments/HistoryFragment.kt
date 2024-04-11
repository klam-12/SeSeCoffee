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
import com.example.sesecoffee.adapters.HistoryAdapter
import com.example.sesecoffee.databinding.FragmentHistoryBinding
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderTrackingViewModel
import kotlinx.coroutines.flow.collectLatest

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var orderTrackingViewModel: OrderTrackingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderTrackingViewModel = (activity as MainActivity).orderTrackingViewModel

        setUpRecyclerViewOrders()
        lifecycleScope.launchWhenStarted {
            orderTrackingViewModel.historyOrderItems.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        println("ALOLOALO $it")
                        historyAdapter.differ.submitList(it.data)
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
        historyAdapter = HistoryAdapter(null);
        binding.historyList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
