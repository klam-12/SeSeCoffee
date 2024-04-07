package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sesecoffee.enums.HotCold
import com.example.sesecoffee.enums.Milk
import com.example.sesecoffee.enums.Size
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sesecoffee.utils.Constant.PRODUCT_COLLECTION
import com.example.sesecoffee.viewModel.OrderViewModel
import java.util.UUID

class ProductOrderActivity : AppCompatActivity() {
    private var quantity = 1
    private var price = 0
    private var sizeFee = 0
    private var milkFee = 0
    private  var productName=""
    private  var productImg=""
    lateinit var idOrder:String

    var db = FirebaseFirestore.getInstance()
    var collectionReference: CollectionReference = db.collection(PRODUCT_COLLECTION)
    var collectionOrders:CollectionReference=db.collection("Orders")

    lateinit var viewModel: OrderItemsViewModel
    lateinit var orderViewModel: OrderViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_order)
        viewModel = OrderItemsViewModel(application)
        orderViewModel = OrderViewModel(application)
        val productId = intent.getStringExtra("productId")
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





        try {
            collectionReference
                .document(productId.toString())
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                       productName=it.getString("name").toString()
                       val priceTemp=it.get("price").toString().toInt()
                       productImg=it.getString("imageUrl").toString()
                        productNameTextView.setText(productName)
                        Glide.with(this).load(productImg).into(productImage)
                        price=priceTemp
                        quantityTextView.setText("$quantity")
                        priceTextView.setText("${price * quantity}VNĐ")
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
            var temperature : String
            var size : String
            var milk : String

            temperature = when(resources.getResourceEntryName(hotColdChoice)){
                "hot" -> HotCold.HOT.value
                else -> HotCold.COLD.value
            }
            size = when(resources.getResourceEntryName(sizeChoice)){
                "smallSize" -> Size.SMALL.value
                "mediumSize" -> Size.MEDIUM.value
                else -> Size.LARGE.value
            }
            milk = when(resources.getResourceEntryName(milkChoice)){
                "noMilk" -> Milk.NOMILK.value
                "smallMilk" -> Milk.SMALLMILK.value
                else -> Milk.LARGEMILK.value
            }
            Log.d("YourActivity", "Your variable value: $productName")
            val tempName=productName
            val tempImg=productImg

            //doc du lieu tren bang ordersHoang kiem tra co dong nao chua userid va done ==false




            val query=collectionOrders.whereEqualTo("userId", UserSingleton.instance?.id.toString())
                .whereEqualTo("done",false)
            query.get()
                .addOnSuccessListener { documentSnapshot->
                    if(documentSnapshot.isEmpty){
                        //neu khong co thi tao moi
                        val id = UUID.randomUUID().toString()
                        val newOrders= Order(id,0,
                            null,
                            UserSingleton.instance?.address,
                            UserSingleton.instance?.id,
                            "",
                            "",
                            "",
                            false,
                            false
                        )
                        orderViewModel.addOrders(newOrders)
                        idOrder=id
                    }
                    else{
                        //lay id cua ordersHoang sau do tao orderItem
                        for(document in documentSnapshot){
                            idOrder= document.getString("id").toString()
                        }
                        Log.i("R","sai oi")
                    }
                    val newOrder = OrderItem(productId, tempName, tempImg, temperature, size, milk, quantity, totalPrice, false)
                    viewModel.addOrderItem(newOrder,idOrder)
                    val intent = Intent(
                        applicationContext,
                        CartOrderActivity::class.java
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)

                }.addOnFailureListener { exception ->
                    // Handle any errors that occur
                    println("Error getting documents: $exception")
                }




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