package com.example.sesecoffee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.adapters.AdminRatingAdapter
import com.example.sesecoffee.adapters.AdminRedeemAdapter
import com.example.sesecoffee.databinding.FragmentAdminRatingBinding

class AdminRatingFragment : Fragment(R.layout.fragment_admin_rating) {
    private var _binding : FragmentAdminRatingBinding? = null
    private val binding get() = _binding!!
    lateinit var ratingAdapter: AdminRatingAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminRatingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerViewRating()

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