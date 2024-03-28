package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sesecoffee.adapters.ProductAdapter
import com.example.sesecoffee.databinding.ActivityHomeBinding
import com.example.sesecoffee.model.Product
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class HomeActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeBinding

    // Firebase References
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var user : FirebaseUser
    var db = FirebaseFirestore.getInstance()
    var collectionReference: CollectionReference = db.collection("Products")

    lateinit var storageReference: StorageReference
    lateinit var productList : MutableList<Product>
    lateinit var productAdapter: ProductAdapter
    lateinit var noProductTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)

        // Firebase Auth
//        firebaseAuth = Firebase.auth
//        user = firebaseAuth.currentUser!!

        // RecyclerView
        binding.rvListProducts.setHasFixedSize(true)
        binding.rvListProducts.layoutManager = LinearLayoutManager(this)

        // Products arrayList
        productList = arrayListOf<Product>()
        setUp()
    }

    override fun onStart() {
        super.onStart()

        // Set up the recyclerview products
        try {
            collectionReference.get()
                .addOnSuccessListener {

                    if (!it.isEmpty) {
                        it.forEach {
                            // convert snapshots to product
                            var product = it.toObject(Product::class.java)

                            productList.add(product)
                        }
                        productAdapter = ProductAdapter(this, productList)
                        binding.rvListProducts.adapter = productAdapter
                        productAdapter.notifyDataSetChanged()
                    } else {
                        binding.tvNoProduct.visibility = View.VISIBLE
                    }
                }.addOnFailureListener() {
                    Toast.makeText(
                        this,
                        "Errors happen when loading the database",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }catch (t:Throwable){
            Log.i("Err", "$t")
        }
    }

    private fun setUp(){
        navigateToCart()
        navigateToProfile()
    }

    private fun navigateToProfile(){
        binding.profileButton.setOnClickListener(){
//            if(user != null && firebaseAuth != null){
                val intent = Intent(this,ProfileActivity::class.java)
                startActivity(intent)
//            }
        }
    }

    private fun navigateToCart(){
        binding.cartButton.setOnClickListener(){
//            if(user != null && firebaseAuth != null){
                // Link to cart
                Toast.makeText(this,"Go to cart implementation",Toast.LENGTH_LONG).show()
//            }
        }
    }
}