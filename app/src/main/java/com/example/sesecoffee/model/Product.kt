package com.example.sesecoffee.model

import android.net.Uri
import com.google.firebase.Timestamp
import java.util.Date
import java.util.UUID

data class Product(
//    val id:String,
    var name:String? = null,
    var price : Int? = null,
    var imageUrl: String? = null,
    var description:String? = null,
    val createdAt: Timestamp? = null,
    ) {
//    constructor() :
//            this(UUID.randomUUID().toString(),"",0,10000,"","",Date(),Date()) {}
//
//    constructor(proName:String) :
//            this(UUID.randomUUID().toString(),proName,0,10000,"","",Date(),Date()) {}

}