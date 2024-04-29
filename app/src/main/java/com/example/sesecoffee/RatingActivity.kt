package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.example.sesecoffee.utils.Constant
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RatingActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    var collectionOrders: CollectionReference = db.collection(Constant.ORDER_COLLECTION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        val intent = intent
        val orderId = intent.getStringExtra("orderIdRating")

        val rating = findViewById<RatingBar>(R.id.ratingBar)
        val comment = findViewById<EditText>(R.id.feedbackET)

        findViewById<Button>(R.id.submitRatingBtn).setOnClickListener {
            val ratingValue = rating.rating
            val commentValue = comment.text.toString()
            if(ratingValue == 0F || commentValue.isEmpty()){
                Toast.makeText(this, "Please rate and feedback the order", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            collectionOrders.document(orderId!!).update("rating", rating.rating)
            collectionOrders.document(orderId).update("comment", comment.text.toString())

            val intent = Intent(
                applicationContext,
                MainActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}