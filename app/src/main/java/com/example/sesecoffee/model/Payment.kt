package com.example.sesecoffee.model

import com.example.sesecoffee.enums.PaymentMethod
import com.example.sesecoffee.enums.PaymentStatus
import com.google.firebase.Timestamp

class Payment(
    var method : PaymentMethod? = null,
    var status : PaymentStatus? = null,
    var paidAt : Timestamp? = null,
) {
    constructor() :
            this(null, PaymentStatus.UNPAID, Timestamp.now()) {}
    constructor(method: PaymentMethod, status: PaymentStatus) :
            this(method, status, Timestamp.now()) {}
}