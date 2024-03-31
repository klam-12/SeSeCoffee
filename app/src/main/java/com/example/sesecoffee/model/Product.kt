package com.example.sesecoffee.model

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Product(
//    val id:String,
    var name:String? = null,
    var price : Int? = null,
    var imageUrl: String? = null,
    val createdAt: Timestamp? = null,
    ) : Parcelable {
//    constructor() :
//            this(UUID.randomUUID().toString(),"",0,10000,"","",Date(),Date()) {}
//
//    constructor(proName:String) :
//            this(UUID.randomUUID().toString(),proName,0,10000,"","",Date(),Date()) {}

}