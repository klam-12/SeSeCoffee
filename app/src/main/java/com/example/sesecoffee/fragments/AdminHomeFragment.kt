package com.example.sesecoffee.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sesecoffee.AddProductActivity
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.SignInActivity
import com.example.sesecoffee.adapters.ProductAdapter
import com.example.sesecoffee.databinding.FragmentAdminHomeBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.google.firebase.firestore.firestoreSettings
import kotlinx.coroutines.flow.collectLatest

/**
 * A simple [Fragment] subclass.
 * Use the [AdminHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminHomeFragment : Fragment(R.layout.fragment_admin_home) {
    private var _binding : FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var productAdapter: ProductAdapter
    lateinit var productsViewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsViewModel = (activity as AdminMainActivity).productsViewModel

        setUpRecyclerViewProducts()
        binding.username.text = UserSingleton.instance?.fullName
        lifecycleScope.launchWhenStarted {
            productsViewModel.fetchAllProducts()
            productsViewModel.products.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        productAdapter.differ.submitList(it.data)

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

        binding.addProductBtn.setOnClickListener(){
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }

        binding.signOutBtn.setOnClickListener(){
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
        }

    }

    private fun hideLoading() {
        binding.adminHomeProgressBar.visibility = View.GONE

    }

    private fun showLoading() {
        binding.adminHomeProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerViewProducts(){
        // Set up the recyclerview products
        productAdapter = ProductAdapter()
        binding.rvListProducts.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = productAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}