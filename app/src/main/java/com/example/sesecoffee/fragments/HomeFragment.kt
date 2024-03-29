package com.example.sesecoffee.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.CartOrderActivity
import com.example.sesecoffee.ProductOrderActivity
import com.example.sesecoffee.ProfileActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.ProductAdapter
import com.example.sesecoffee.databinding.FragmentHomeBinding
import com.example.sesecoffee.model.Product
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(R.layout.fragment_home){
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Firebase References
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var user : FirebaseUser
    var db = FirebaseFirestore.getInstance()
    var collectionReference: CollectionReference = db.collection("Products")

    lateinit var storageReference: StorageReference
    lateinit var productList : MutableList<Product>
    lateinit var productAdapter: ProductAdapter
    lateinit var noProductTextView : TextView


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
        binding.homeProgressBar.visibility = View.VISIBLE
        binding.homeProgressBar.progress
        // Products arrayList
        productList = arrayListOf<Product>()
        binding.rvListProducts.setHasFixedSize(true)
        binding.rvListProducts.layoutManager = GridLayoutManager(
            binding.root.context,2
        )
        setUp()
        binding.homeProgressBar.visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()
        setUpRecyclerViewProducts()
    }

    private fun setUp(){
        navigateToCart()
        navigateToProfile()
    }
    private fun setUpRecyclerViewProducts(){
        // Set up the recyclerview products
        try {
            collectionReference.get()
                .addOnSuccessListener {
                    productList.clear()
                    if (!it.isEmpty) {
                        it.forEach {
                            // convert snapshots to product
                            var product = it.toObject(Product::class.java)
                            productList.add(product)
                        }

                        productAdapter = ProductAdapter(binding.root.context, productList)
                        binding.rvListProducts.adapter = productAdapter
                        productAdapter.notifyDataSetChanged()
                    } else {
                        binding.tvNoProduct.visibility = View.VISIBLE
                    }
                }.addOnFailureListener() {
                    Toast.makeText(
                        binding.root.context,
                        "Errors happen when loading the database",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }catch (t:Throwable){
            Log.i("Err", "$t")
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