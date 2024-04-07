package com.example.sesecoffee.model

import com.example.sesecoffee.enums.HotCold
import com.example.sesecoffee.enums.Milk
import com.example.sesecoffee.enums.Size

data class OrderItem(
    val id:String? = null,
    var userId:String? = null,
    var productName : String? = null,
    var productImage: String? = null,
    var hotCold : String? = null,
    var size : String? = null,
    var milk : String? = null,
    var quantity : Int? = null,
    var price : Int? = null,
    var isDelivered : Boolean? = false,
) {
    constructor(
        id: String?,
        userId: String?,
        productName : String?,
        productImage : String?,
        hotCold : HotCold?,
        size : Size?,
        milk : Milk?,
        quantity : Int?,
        price : Int?,
        isDelivered : Boolean?
    ) :
            this(id, userId, productName, productImage, hotCold.toString(), size.toString(), milk.toString(), quantity, price, isDelivered) {}

    override fun toString() : String {
        return "$productName $hotCold $size $milk $quantity $price"
    }
}