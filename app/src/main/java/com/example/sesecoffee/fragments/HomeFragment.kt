package com.example.sesecoffee.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sesecoffee.CartOrderActivity
import com.example.sesecoffee.MainActivity
import com.example.sesecoffee.ProfileActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.ProductAdapter
import com.example.sesecoffee.databinding.FragmentHomeBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest


class HomeFragment : Fragment(R.layout.fragment_home){
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Firebase References
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var user : FirebaseUser
    var db = FirebaseFirestore.getInstance()
    var collectionReference: CollectionReference = db.collection("Products")

    lateinit var productList : MutableList<Product>
    lateinit var productAdapter: ProductAdapter
    lateinit var productsViewModel: ProductsViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsViewModel = (activity as MainActivity).productsViewModel

        setUpRecyclerViewProducts()
        lifecycleScope.launchWhenStarted {
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
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        setUpButtons()
    }

    private fun hideLoading() {
        binding.homeProgressBar.visibility = View.GONE

    }

    private fun showLoading() {
        binding.homeProgressBar.visibility = View.VISIBLE
    }

    private fun setUpButtons(){
        navigateToCart()
        navigateToProfile()
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

    private fun navigateToProfile(){
        binding.profileButton.setOnClickListener(){
//            if(user != null && firebaseAuth != null){
            val intent = Intent(binding.root.context,ProfileActivity::class.java)
            startActivity(intent)
//            }
        }
    }

    private fun navigateToCart(){
        binding.cartButton.setOnClickListener(){
//            if(user != null && firebaseAuth != null){
            // Link to cart
            val intent = Intent(binding.root.context, CartOrderActivity::class.java)
            startActivity(intent)
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}