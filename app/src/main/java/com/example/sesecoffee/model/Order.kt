package com.example.sesecoffee.model

import com.google.firebase.Timestamp

data class Order(
    val id:String? = null,
    var total : Int? = null,
    var createAt : Timestamp? = null,
    var address : String? = null,
    var userId : String? = null,
    var phoneNumber : String? = null,
    var paymentId: String?=null,
    var code : String? = null,
    var done: Boolean= false,
    var delivered: Boolean=false
) {
 //   constructor() :
    //        this(id,0, null, "", "","", "", "",false,false) {}
//    constructor(total: Int, paymentId: String, address: String, userId: String, phoneNumber: String, code: String) :
//            this(total, Timestamp.now(), paymentId, address, userId, phoneNumber, code) {}
}