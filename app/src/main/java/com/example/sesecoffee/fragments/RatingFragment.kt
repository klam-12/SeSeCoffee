package com.example.sesecoffee.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.MainActivity
import com.example.sesecoffee.PersonalRatings
import com.example.sesecoffee.ProfileActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.AdminRatingAdapter
import com.example.sesecoffee.databinding.FragmentAdminRatingBinding
import com.example.sesecoffee.databinding.FragmentRatingBinding
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderViewModel
import kotlinx.coroutines.flow.collectLatest
import java.math.RoundingMode
import java.text.DecimalFormat


class RatingFragment : Fragment(R.layout.fragment_rating) {
    private var _binding : FragmentRatingBinding? = null
    private val binding get() = _binding!!
    lateinit var ratingAdapter: AdminRatingAdapter
    lateinit var orderViewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRatingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderViewModel = (activity as MainActivity).orderViewModel
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
                        val filteredListData = listData?.filter { it.rating != 0F }
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.CEILING
                        val overallRatingText = df.format(calculateOverallRating(filteredListData!!))
                        binding.avgRating.text = "${overallRatingText}/5"

                        ratingAdapter.differ.submitList(filteredListData)
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

        binding.btnPRatings.setOnClickListener(){
            val intent = Intent(binding.root.context, PersonalRatings::class.java)
            startActivity(intent)
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

    private fun calculateOverallRating(data : List<Order>) : Float {
        var sumRates = 0F;
        var countRates = 0;
        data.forEach { order ->
            if(order.rating != 0F){
                countRates += 1
                sumRates += order.rating!!
            }
        }
        var avgRate = 0F
        if(countRates != 0){
            avgRate = sumRates/countRates
        }

        return avgRate;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}