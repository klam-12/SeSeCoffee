package com.example.sesecoffee.model

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Product(
    val id:String? = null,
    var name:String? = null,
    var price : Int? = null,
    var imageUrl: String? = null,
    val createdAt: Timestamp? = null,
    ) : Parcelable {
//    val productName: String
//        get() = name ?: ""
//
//    val productPrice: Int
//        get() = price ?: 0
//
//    val productImageUrl: String
//        get() = imageUrl ?: ""
//
//    val productCreatedAt: Timestamp
//        get() = createdAt ?: Timestamp.now()

//    fun setName(name: String?) {
//        this.name = name
//    }
//
//    fun setPrice(price: Int?) {
//        this.price = price
//    }
//
//    fun setImageUrl(imageUrl: String?) {
//        this.imageUrl = imageUrl
//    }


}