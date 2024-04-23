package com.example.sesecoffee.model

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale


data class Order(
    val id:String? = null,
    var total : Int? = null,
    var createAt : Timestamp? = null,
    var address : String? = null,
    var userId : String? = null,
    var phoneNumber : String? = null,
    var paymentMethod : String? = null,
    var stripeCustomerId : String? = null,
    var done: Boolean= false,
    var delivered: Boolean=false,
    var rating : Int? = null,
    var comment : String? = null
) {

}