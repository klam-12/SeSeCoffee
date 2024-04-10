package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sesecoffee.enums.HotCold
import com.example.sesecoffee.enums.Milk
import com.example.sesecoffee.enums.Size
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.ORDER_COLLECTION
import com.example.sesecoffee.utils.Constant.ORDER_ITEM_COLLECTION
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ProductUpdateOrderActivity : AppCompatActivity() {
    private var quantity = 1
    private var price = 0
    private var sizeFee = 0
    private var milkFee = 0
    private var image = ""

    private lateinit var orderItem : OrderItem
    private lateinit var viewModel: OrderItemsViewModel

    var db = FirebaseFirestore.getInstance()
    var collectionOrders:CollectionReference = db.collection(ORDER_COLLECTION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_order)
        viewModel = OrderItemsViewModel(application)

        val intent = intent
        val orderId = intent.getStringExtra("order")

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
            collectionOrders.whereEqualTo("userId", UserSingleton.instance?.id.toString())
                .whereEqualTo("done",false).get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        val idOrder = it.documents[0].id
                        collectionOrders.document(idOrder).collection(ORDER_ITEM_COLLECTION).whereEqualTo("id", orderId).get()
                            .addOnSuccessListener {
                                it.forEach {
                                    showLoading()

                                    orderItem = it.toObject(OrderItem::class.java)!!
                                    productNameTextView.setText(orderItem.productName)
                                    Glide.with(this).load(orderItem.productImage).into(productImage)

                                    when(orderItem.hotCold){
                                        HotCold.HOT.value -> hotColdRadioGroup.check(R.id.hot)
                                        HotCold.COLD.value -> hotColdRadioGroup.check(R.id.cold)
                                    }
                                    when(orderItem.size){
                                        Size.SMALL.value -> sizeRadioGroup.check(R.id.smallSize)
                                        Size.MEDIUM.value -> sizeRadioGroup.check(R.id.mediumSize)
                                        Size.LARGE.value -> sizeRadioGroup.check(R.id.largeSize)
                                    }
                                    when(orderItem.milk) {
                                        Milk.NOMILK.value -> milkRadioGroup.check(R.id.noMilk)
                                        Milk.SMALLMILK.value -> milkRadioGroup.check(R.id.smallMilk)
                                        Milk.LARGEMILK.value -> milkRadioGroup.check(R.id.largeMilk)
                                    }

                                    if(orderItem.size == Size.SMALL.value){
                                        sizeFee = 0
                                    } else if(orderItem.size == Size.MEDIUM.value){
                                        sizeFee = 1000
                                    } else {
                                        sizeFee = 2000
                                    }

                                    if(orderItem.milk == Milk.NOMILK.value){
                                        milkFee = 0
                                    } else if(orderItem.milk == Milk.SMALLMILK.value){
                                        milkFee = 1000
                                    } else {
                                        milkFee = 2000
                                    }

                                    image = orderItem.productImage!!
                                    price = orderItem.price!! / orderItem.quantity!!
                                    quantity = orderItem.quantity!!
                                    quantityTextView.setText("${orderItem.quantity}")
                                    priceTextView.setText("${orderItem.price}VNĐ")

                                    hideLoading()

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

                                        val newOrder = OrderItem(orderItem.id, orderItem.productId, productNameTextView.text.toString(), image, temperature, size, milk, quantity, totalPrice, false)
                                        viewModel.updateOrderItemInfo(newOrder, idOrder)

                                        val intent = Intent(
                                            applicationContext,
                                            CartOrderActivity::class.java
                                        )
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(intent)
                                    }
                                }
                            }
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

    private fun hideLoading() {
        findViewById<ProgressBar>(R.id.orderProgressBar).visibility = View.GONE
    }

    private fun showLoading() {
        findViewById<ProgressBar>(R.id.orderProgressBar).visibility = View.VISIBLE
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