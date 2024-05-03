package com.example.sesecoffee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.sesecoffee.enums.PaymentMethod
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.ORDER_COLLECTION
import com.example.sesecoffee.utils.Constant.ORDER_ITEM_COLLECTION
import com.example.sesecoffee.utils.Format
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject


class PaymentActivity : AppCompatActivity() {
    private lateinit var userOrderId : String
    private lateinit var userName : String
    private lateinit var userPhone : String
    private lateinit var userAddress : String
    private var totalPrice = 0

    var format: Format = Format()

    var db = FirebaseFirestore.getInstance()
    var collectionOrders: CollectionReference = db.collection(ORDER_COLLECTION)

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

        PaymentConfiguration.init(this, PUBLISH_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        val avatar = findViewById<ShapeableImageView>(R.id.paymentAvatar)
        val name = findViewById<TextView>(R.id.paymentName)
        val phone = findViewById<TextView>(R.id.paymentPhone)
        val address = findViewById<TextView>(R.id.paymentAddress)
        val price = findViewById<TextView>(R.id.paymentPrice)
        val amount = findViewById<TextView>(R.id.paymentAmount)
        setUpRadioButton()

        val query = collectionOrders.whereEqualTo("userId", UserSingleton.instance?.id.toString())
            .whereEqualTo("done",false)
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
                            val orderItemList = result.toObjects(OrderItem::class.java)
                            totalPrice = calculateTotalPrice(orderItemList)
                            price.setText(format.formatToDollars(totalPrice))
                            amount.setText(format.formatToDollars(totalPrice))
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
                                val paidOrder = Order(
                                    userOrderId,
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

                                collectionOrders.document(userOrderId).set(paidOrder)
                                val intent = Intent(
                                    applicationContext,
                                    SuccessOrderActivity::class.java
                                )
                                intent.putExtra("orderId", userOrderId)
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
        val paidOrder = Order(
            userOrderId,
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

        collectionOrders.document(userOrderId).set(paidOrder)
        val intent = Intent(
            applicationContext,
            SuccessOrderActivity::class.java
        )
        intent.putExtra("orderId", userOrderId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}