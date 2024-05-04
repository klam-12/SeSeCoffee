package com.example.sesecoffee.model

import com.example.sesecoffee.enums.HotCold
import com.example.sesecoffee.enums.Milk
import com.example.sesecoffee.enums.Size
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class OrderItem(
    val id:String? = null,
    var productId:String? = null,
    var productName : String? = null,
    var productImage: String? = null,
    var description: String? = null,
    var hotCold : String? = null,
    var size : String? = null,
    var milk : String? = null,
    var quantity : Int? = null,
    var price : Int? = null,
    var isDelivered : Boolean? = false,
) {
    constructor(
        id: String?,
        productId: String?,
        productName : String?,
        productImage : String?,
        description: String?,
        hotCold : HotCold?,
        size : Size?,
        milk : Milk?,
        quantity : Int?,
        price : Int?,
        isDelivered : Boolean?
    ) :
            this(id, productId, productName, productImage, description, hotCold.toString(), size.toString(), milk.toString(), quantity, price, isDelivered) {}

    override fun toString() : String {
        return "$productId $productName $hotCold $size $milk $quantity $price "
    }
    fun getReward(): Int? {
        return (price?.toDouble() )?.div(1)?.let { Math.round(it).toInt() }
    }




}