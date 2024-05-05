package com.example.sesecoffee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.sesecoffee.adapters.PaymentItemsAdapter
import com.example.sesecoffee.enums.PaymentMethod
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.ORDER_COLLECTION
import com.example.sesecoffee.utils.Constant.ORDER_ITEM_COLLECTION
import com.example.sesecoffee.utils.Format
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject
import java.util.UUID


class PaymentReOrderActivity : AppCompatActivity() {
    private lateinit var userOrderId : String
    private lateinit var userName : String
    private lateinit var userPhone : String
    private lateinit var userAddress : String
    private lateinit var orderItemList: List<OrderItem>
    private lateinit var orderAdapter: PaymentItemsAdapter
    private var totalPrice = 0
    private lateinit var orderItemViewModel: OrderItemsViewModel
    var format: Format = Format()

    var db = FirebaseFirestore.getInstance()
    var collectionOrders: CollectionReference = db.collection(ORDER_COLLECTION)
    val collectionUser: CollectionReference = db.collection("USER")
    //Stripe Configuration
    val PUBLISH_KEY = "pk_test_51P8ErMAejLzzD81q7DpOJ0CKA0r29wddEi5E6RK6tXhrFuv5k6AmJuIqX3CIn5iphfYkrrOfvSrgSh0YDL35KnoQ00w70NZRIR"
    val SECRET_KEY = "sk_test_51P8ErMAejLzzD81qcswuJh9q2TamC1nKmwnAZNohNDHFfLcsWXAZoKhWsm3bawPvM89sZ11PUbV7Mn8rzIfWoxim00lzmVpMda"
    lateinit var paymentSheet : PaymentSheet
    var customerId : String = ""
    var ephemeralKey : String = ""
    var ephemeralSecret : String = ""
    var clientSecret : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        orderItemViewModel = OrderItemsViewModel(application)

        val intent = intent
        val orderId = intent.getStringExtra("orderId")
        PaymentConfiguration.init(this, PUBLISH_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        orderAdapter = PaymentItemsAdapter(this)

        val name = findViewById<TextView>(R.id.paymentName)
        val phone = findViewById<TextView>(R.id.paymentPhone)
        val address = findViewById<TextView>(R.id.paymentAddress)
        val price = findViewById<TextView>(R.id.paymentPrice)
        val paymentRecyclerView = findViewById<RecyclerView>(R.id.paymentItems)
        paymentRecyclerView.setHasFixedSize(true)
        paymentRecyclerView.layoutManager = LinearLayoutManager(this)
        setUpRadioButton()

        val query = collectionOrders.whereEqualTo("id", orderId)
        query.get()
            .addOnSuccessListener { documentSnapshot->
                if(!documentSnapshot.isEmpty){
                    var order = documentSnapshot.toObjects(Order::class.java)
                    userName = UserSingleton.instance?.fullName.toString()
                    userOrderId = order[0].id.toString()
                    userPhone = UserSingleton.instance?.phone.toString()
                    userAddress = UserSingleton.instance?.address.toString()

                    collectionOrders.document(userOrderId).collection(ORDER_ITEM_COLLECTION).get()
                        .addOnSuccessListener { result ->

                            orderItemList = result.toObjects(OrderItem::class.java)


                            orderAdapter.differ.submitList(orderItemList)
                            paymentRecyclerView.adapter = orderAdapter

                            totalPrice = calculateTotalPrice(orderItemList)
                            price.setText(format.formatToDollars(totalPrice))
                        }.addOnFailureListener { exception ->
                            println("Error getting documents: $exception")
                        }

                    name.setText(this.userName)
                    phone.setText("Phone: ${this.userPhone}")
                    address.setText("Address: ${this.userAddress}")

                    findViewById<Button>(R.id.paymentProceedBtn).setOnClickListener {
                        val paymentMethodChoice = getCheckedRadioButtonId()
                        if(paymentMethodChoice == -1) {
                            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        if(userAddress == "" || userPhone == "") {
                            Toast.makeText(this, "Please update your information in Profile", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        when(resources.getResourceEntryName(paymentMethodChoice)) {
                            "cash" -> {

                                val id = UUID.randomUUID().toString()
                                val paidOrder = Order(
                                    id,
                                    totalPrice,
                                    Timestamp.now(),
                                    userAddress,
                                    UserSingleton.instance?.id.toString(),
                                    userPhone,
                                    PaymentMethod.CASH.value,
                                    "",
                                    true,
                                    false,
                                    0F,
                                    ""
                                )
                                val redeemPoint = UserSingleton.instance?.redeemPoint!! + totalPrice
                                collectionUser.document(UserSingleton.instance?.id.toString()).update("redeemPoint", redeemPoint)
                                UserSingleton.instance?.redeemPoint = redeemPoint



                                collectionOrders.document(id).set(paidOrder)
                                var newOrder = copyOrderItemList(orderItemList)

                                for (orderItem in newOrder) {
                                        orderItemViewModel.addOrderItem(orderItem, id)
                                }

                                val intent = Intent(
                                    applicationContext,
                                    SuccessOrderActivity::class.java
                                )
                                intent.putExtra("orderId", id)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                            else -> {
                                createCustomerId()
                            }
                        }
                    }
                }

            }.addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        findViewById<ImageButton>(R.id.paymentBackBtn).setOnClickListener {
            finish()
        }
    }

    fun copyOrderItemList(originalList: List<OrderItem>): List<OrderItem> {
        val copiedList = mutableListOf<OrderItem>()
        for (item in originalList) {
            val newId = UUID.randomUUID().toString() // Tạo một ID mới cho mỗi mục đơn hàng sao chép
            val copiedItem = OrderItem(
                newId,
                item.productId,
                item.productName,
                item.productImage,
                item.description,
                item.hotCold,
                item.size,
                item.milk,
                item.quantity,
                item.price,
                false
            )
            copiedList.add(copiedItem)
        }
        return copiedList
    }

    private fun showLoading() {
        findViewById<LinearLayout>(R.id.loadingContainer).visibility = View.VISIBLE
    }

    private fun hideLoading() {
        findViewById<LinearLayout>(R.id.loadingContainer).visibility = View.GONE
    }

    private fun getCheckedRadioButtonId(): Int {
        val cashRadio = findViewById<RadioButton>(R.id.cash)
        val cardRadio = findViewById<RadioButton>(R.id.creditCard)

        return if (cashRadio.isChecked) {
            R.id.cash
        } else if (cardRadio.isChecked) {
            R.id.creditCard
        } else {
            -1
        }
    }

    private fun setUpRadioButton(){
        val cashRadio = findViewById<RadioButton>(R.id.cash)
        val cardRadio = findViewById<RadioButton>(R.id.creditCard)

        cashRadio.setOnClickListener {
            cashRadio.isChecked = true
            cardRadio.isChecked = false
        }
        cardRadio.setOnClickListener {
            cashRadio.isChecked = false
            cardRadio.isChecked = true
        }
    }

    private fun calculateTotalPrice(itemList : List<OrderItem>) : Int {
        var price = 0
        itemList.forEach {
            price += it.price!!
        }
        return price
    }


    private fun createCustomerId(){
        val request = object : StringRequest(
            Request.Method.POST,
            "https://api.stripe.com/v1/customers",
            { response ->
                try{
                    showLoading()
                    val jsonObject = JSONObject(response)
                    customerId = jsonObject.getString("id")

                    getEphemeralKey(customerId)
                    Log.d("PaymentActivity", "Response: $response")
                } catch (e: Exception){
                    Log.e("PaymentActivity", "Error: $e")
                }
            },
            { error ->
                Log.e("PaymentActivity", "Error: $error")
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRET_KEY"
                return headers
            }
        }
        val requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request)
    }

    private fun getEphemeralKey(customerId: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "https://api.stripe.com/v1/ephemeral_keys",
            { response ->
                try{
                    val jsonObject = JSONObject(response)
                    ephemeralKey = jsonObject.getString("id")
                    ephemeralSecret = jsonObject.getString("secret")

                    getClientSecret(customerId, ephemeralKey)
                    Log.d("PaymentActivity", "Response: $response")
                } catch (e: Exception){
                    Log.e("PaymentActivity", "Error: $e")
                }
            },
            { error ->
                Log.e("PaymentActivity", "Error: $error")
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRET_KEY"
                headers["Stripe-Version"] = "2024-04-10"
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["customer"] = customerId
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request)
    }

    private fun getClientSecret(customerId: String, ephemeralKey: String) {
        val request = object : StringRequest(
            Request.Method.POST,
            "https://api.stripe.com/v1/payment_intents",
            { response ->
                try{
                    val jsonObject = JSONObject(response)
                    clientSecret = jsonObject.getString("client_secret")

                    paymentFlow()
                    Log.d("PaymentActivity", "Response: $response")
                } catch (e: Exception){
                    Log.e("PaymentActivity", "Error: $e")
                }
            },
            { error ->
                Log.e("PaymentActivity", "Error: $error")
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRET_KEY"
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["customer"] = customerId
                params["amount"] = totalPrice.toString() + "00"
                params["currency"] = "usd"
                params["automatic_payment_methods[enabled]"] = "true"
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request)
    }

    private fun paymentFlow() {
        hideLoading()
        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                "SeSeCoffee",
                PaymentSheet.CustomerConfiguration(
                    customerId,
                    ephemeralSecret
                )
            )
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Toast.makeText(this, "Payment succeeded", Toast.LENGTH_LONG).show()
                handleSuccessfulPayment()
            }

            is PaymentSheetResult.Canceled -> {
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_LONG).show()
            }

            is PaymentSheetResult.Failed -> {
                Toast.makeText(
                    this,
                    "Payment failed: ${paymentSheetResult.error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleSuccessfulPayment() {
        val id = UUID.randomUUID().toString()
        val paidOrder = Order(
            id,
            totalPrice,
            Timestamp.now(),
            userAddress,
            UserSingleton.instance?.id.toString(),
            userPhone,
            PaymentMethod.CARD.value,
            customerId,
            true,
            false,
            0F,
            ""
        )
        val redeemPoint = UserSingleton.instance?.redeemPoint!! + totalPrice
        collectionUser.document(UserSingleton.instance?.id.toString()).update("redeemPoint", redeemPoint)
        UserSingleton.instance?.redeemPoint = redeemPoint

        collectionOrders.document(id).set(paidOrder)
        var newOrder = copyOrderItemList(orderItemList)

        for (orderItem in newOrder) {
            orderItemViewModel.addOrderItem(orderItem, id)
        }

        val intent = Intent(
            applicationContext,
            SuccessOrderActivity::class.java
        )
        intent.putExtra("orderId", id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}