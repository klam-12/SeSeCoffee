package com.example.sesecoffee.fragments

import android.content.Intent
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
import com.example.sesecoffee.RedeemActivity
import com.example.sesecoffee.adapters.RewardAdapter
import com.example.sesecoffee.databinding.FragmentRewardBinding
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.RewardItemViewModel
import kotlinx.coroutines.flow.collectLatest

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RewardFragment : Fragment(R.layout.fragment_reward) {
    private var _binding: FragmentRewardBinding? = null
    private val binding get() = _binding!!

    private lateinit var rewardAdapter: RewardAdapter
    private lateinit var rewardItemViewModel: RewardItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rewardItemViewModel = (activity as MainActivity).rewardViewModel
        rewardItemViewModel.fetchAllOrders()

        setUpRecyclerViewOrders()

        lifecycleScope.launchWhenStarted {
            rewardItemViewModel.fetchAllOrders()
            rewardItemViewModel.orders.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        val redeemPoint = UserSingleton.instance?.redeemPoint
                        binding.textView5.text = redeemPoint.toString()
                        hideLoading()
                        if(it.data?.isEmpty() == true) {
                            Toast.makeText(
                                context,
                                "No rewards yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else  rewardAdapter.differ.submitList(it.data)
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
        setUpButtons()
    }
    private fun setUpButtons(){
        navigateToRedeem()

    }

    private fun navigateToRedeem(){
        binding.redeemBtn.setOnClickListener(){
//            if(user != null && firebaseAuth != null){
            // Link to cart
            val intent = Intent(binding.root.context, RedeemActivity::class.java)
            startActivity(intent)
//            }
        }
    }
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE

    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerViewOrders() {
        rewardAdapter = RewardAdapter(null);
        binding.rvListHistoryRewards.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rewardAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}