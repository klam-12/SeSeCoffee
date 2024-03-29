package com.example.sesecoffee.model

import com.example.sesecoffee.enums.HotCold
import com.example.sesecoffee.enums.Milk
import com.example.sesecoffee.enums.Size

data class OrderItem(
    var orderId : String? = null,
    var productId : String? = null,
    var productName : String? = null,
    var hotCold : HotCold? = null,
    var size : Size? = null,
    var milk : Milk? = null,
    var quantity : Int? = null,
    var price : Int? = null,
    var isDelivered : Boolean? = false,
) {
    constructor() :
            this("", "", "", null, null, null, 0, 0, false) {}
    constructor(productName: String, hotCold: HotCold, size: Size, milk: Milk, quantity: Int, price: Int) :
            this("", "", productName, hotCold, size, milk, quantity, price, false) {}
}