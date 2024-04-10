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
//    constructor() :
//            this("",0, null, "", "","", "", "",false,false) {}
//    constructor(id: String, total: Int, timestamp: Timestamp, address: String, userId: String, phoneNumber: String, code: String, done: Boolean) :
//            this(id, total, timestamp, address, userId, phoneNumber, code, done) {}
}