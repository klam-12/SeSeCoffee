package com.example.sesecoffee

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.example.sesecoffee.adapters.ProductAdapter
import com.example.sesecoffee.enums.HotCold
import com.example.sesecoffee.enums.Milk
import com.example.sesecoffee.enums.Size
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProductUpdateOrderActivity : AppCompatActivity() {
    private var quantity = 1
    private var price = 0
    private var sizeFee = 0
    private var milkFee = 0

    private lateinit var product : Product
    var db = FirebaseFirestore.getInstance()
    var collectionReference: CollectionReference = db.collection("Products")

    val firebaseSingleton = FirebaseSingleton.getInstance()
    lateinit var viewModel: OrderItemsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_order)
        viewModel = OrderItemsViewModel(firebaseSingleton, application)

        val intent = intent
        val message = intent.getStringExtra("Order")
        val tokens = message?.split(" ")
        for (token in tokens!!) {
            Log.i("Token", token)
        }

        val productImage = findViewById<ImageView>(R.id.orderImageView)
        val productNameTextView = findViewById<TextView>(R.id.orderItem)
        val quantityTextView = findViewById<TextView>(R.id.orderQuantity)
        val priceTextView = findViewById<TextView>(R.id.orderPrice)

        val hotColdRadioGroup = findViewById<RadioGroup>(R.id.orderHotColdChoice)
        val sizeRadioGroup = findViewById<RadioGroup>(R.id.orderSizeChoice)
        val milkRadioGroup = findViewById<RadioGroup>(R.id.orderMilkChoice)

        val smallSizeRadio = findViewById<RadioButton>(R.id.smallSize)
        val mediumSizeRadio = findViewById<RadioButton>(R.id.mediumSize)
        val largeSizeRadio = findViewById<RadioButton>(R.id.largeSize)
        val noMilkRadio = findViewById<RadioButton>(R.id.noMilk)
        val smallMilkRadio = findViewById<RadioButton>(R.id.smallMilk)
        val largeMilkRadio = findViewById<RadioButton>(R.id.largeMilk)

//        try {
//            collectionReference
//                .whereEqualTo("name", tokens?.get(0))
//                .get()
//                .addOnSuccessListener {
//                    if (!it.isEmpty) {
//                        it.forEach {
//                            product = it.toObject(Product::class.java)
//                            Glide.with(this).load(product.imageUrl).into(productImage)
//                        }
//                    }
//                }.addOnFailureListener() {
//                    Toast.makeText(
//                        this,
//                        "Errors happen when loading the database",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//        }catch (t:Throwable){
//            Log.i("Err", "$t")
//        }

//        productNameTextView.setText(tokens?.get(0) ?: "Product Name")
//        price = tokens.get(5).toInt()
//        quantity = tokens.get(4).toInt()
//        quantityTextView.setText("$quantity")
//        priceTextView.setText("${price * quantity}VNĐ")

//        if(tokens?.get(1) == "Hot"){
//            hotColdRadioGroup.check(R.id.hot)
//        } else {
//            hotColdRadioGroup.check(R.id.cold)
//        }
//
//        if(tokens?.get(2) == "S"){
//            sizeRadioGroup.check(R.id.smallSize)
//        } else if(tokens?.get(2) == "M"){
//            sizeRadioGroup.check(R.id.mediumSize)
//        } else {
//            sizeRadioGroup.check(R.id.largeSize)
//        }
//
//        if(tokens?.get(3) == "No milk"){
//            milkRadioGroup.check(R.id.noMilk)
//        } else if(tokens?.get(3) == "Small milk"){
//            milkRadioGroup.check(R.id.smallMilk)
//        } else {
//            milkRadioGroup.check(R.id.largeMilk)
//        }

        handleRadioButton(smallSizeRadio, 0, 0)
        handleRadioButton(mediumSizeRadio, 1000, 0)
        handleRadioButton(largeSizeRadio, 2000, 0)
        handleRadioButton(noMilkRadio, 0, 1)
        handleRadioButton(smallMilkRadio, 1000, 1)
        handleRadioButton(largeMilkRadio, 2000, 1)

        findViewById<Button>(R.id.orderQuantityPlus).setOnClickListener {
            quantity++
            quantityTextView.setText("$quantity")
            priceTextView.setText("${price * quantity}VNĐ")
        }

        findViewById<Button>(R.id.orderQuantityMinus).setOnClickListener {
            quantity--
            if(quantity < 1){
                quantity = 1
            }
            quantityTextView.setText("$quantity")
            priceTextView.setText("${price * quantity}VNĐ")
        }

        findViewById<Button>(R.id.orderNextBtn).setOnClickListener {
            val hotColdChoice = hotColdRadioGroup.checkedRadioButtonId
            val sizeChoice = sizeRadioGroup.checkedRadioButtonId
            val milkChoice = milkRadioGroup.checkedRadioButtonId

            if (sizeChoice == -1 || milkChoice == -1 || hotColdChoice == -1) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityTextView.text.toString().toInt()
            val totalPrice = price * quantity
            var temperature = resources.getResourceEntryName(hotColdChoice)
            var size = resources.getResourceEntryName(sizeChoice)
            var milk = resources.getResourceEntryName(milkChoice)

            val intent = Intent(
                applicationContext,
                CartOrderActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.orderBackBtn).setOnClickListener {
            finish()
        }

        findViewById<ImageButton>(R.id.orderCartBtn).setOnClickListener {
            val intent = Intent(
                applicationContext,
                CartOrderActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    fun handleRadioButton(radio: RadioButton, fee: Int, type: Int) {
        radio.setOnClickListener {
            if(type == 0)
                addSizeFee(fee)
            else if(type == 1){
                addMilkFee(fee)
            }
            findViewById<TextView>(R.id.orderPrice).setText("${price * quantity}VNĐ")
        }
    }
    fun addSizeFee(fee: Int): Int {
        price -= sizeFee
        sizeFee = fee
        price += sizeFee
        return price * quantity
    }

    fun addMilkFee(fee: Int): Int {
        price -= milkFee
        milkFee = fee
        price += milkFee
        return price * quantity
    }
}