package com.example.sesecoffee.model

import com.google.firebase.Timestamp

data class Order(
    var total : Int? = null,
    var createAt : Timestamp? = null,
    var paymentId : String? = null,
    var address : String? = null,
    var userId : String? = null,
    var phoneNumber : String? = null,
    var code : String? = null,
) {
    constructor() :
            this(0, Timestamp.now(), "", "","", "", "") {}
    constructor(total: Int, paymentId: String, address: String, userId: String, phoneNumber: String, code: String) :
            this(total, Timestamp.now(), paymentId, address, userId, phoneNumber, code) {}
}