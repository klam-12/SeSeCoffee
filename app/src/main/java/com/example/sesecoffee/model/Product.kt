package com.example.sesecoffee.model

import java.util.Date
import java.util.UUID

data class Product(
    val id:String,
    var name:String,
    var imageUrl:Int,
    var price : Int,
    var description:String,
    var category: String,
    val createdAt: Date,
    var updatedAt: Date,
    ) {
    constructor() :
            this(UUID.randomUUID().toString(),"",0,10000,"","",Date(),Date()) {}

    constructor(proName:String) :
            this(UUID.randomUUID().toString(),proName,0,10000,"","",Date(),Date()) {}
}