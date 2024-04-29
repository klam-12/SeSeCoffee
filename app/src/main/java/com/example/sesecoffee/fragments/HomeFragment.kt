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
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.sesecoffee.CartOrderActivity
import com.example.sesecoffee.MainActivity
import com.example.sesecoffee.ProfileActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.ProductAdapter
import com.example.sesecoffee.databinding.FragmentHomeBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.UserSingleton
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

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/sese-coffee.appspot.com/o/product_images%2Fsesecoffee.png?alt=media&token=a009190c-b0d8-49d9-943e-33c031f73f24"))
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/sese-coffee.appspot.com/o/product_images%2Fcoffee_on_table.jpg?alt=media&token=4117e65d-ea79-4cbf-b368-95557ed2bb86"))
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/sese-coffee.appspot.com/o/product_images%2Fexpresso.jpg?alt=media&token=73b82aa4-e305-4008-ae33-1473306ec805"))
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/sese-coffee.appspot.com/o/product_images%2Fcappucino.png?alt=media&token=887876d2-774d-4621-927a-971d25d28b8a"))

        setUpRecyclerViewProducts()
        binding.username.text = UserSingleton.instance?.fullName
        binding.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
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
        productsViewModel.fetchAllProducts()
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