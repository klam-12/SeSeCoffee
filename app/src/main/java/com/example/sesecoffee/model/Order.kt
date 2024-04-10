package com.example.sesecoffee.model

import com.google.firebase.Timestamp


data class Order(
    val id:String? = null,
    var total : Int? = null,
    var createAt : Timestamp? = null,
    var address : String? = null,
    var userId : String? = null,
    var phoneNumber : String? = null,
    var paymentMethod : String? = null,
    var code : String? = null,
    var done: Boolean= false,
    var delivered: Boolean=false
) {

}