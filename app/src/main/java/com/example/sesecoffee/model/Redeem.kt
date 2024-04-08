package com.example.sesecoffee.model

import java.util.Date

data class Redeem (
    var id: String? = null,
    var productId : String? = null,
    var imageUrl: String? = null,
    var productName: String? = null,
    var point: Int? = null,
    var untilAt: Date? = null
){
}