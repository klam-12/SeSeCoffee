package com.example.sesecoffee

import java.util.Date
import java.util.UUID

class Product(
    val id:String = UUID.randomUUID().toString(),
    var name:String,
    var image:String,
    var description:String,
    var category: String,
    val createdAt: Date,
    var updatedAt: Date,
    ) {
    constructor() :
            this(UUID.randomUUID().toString(),"","","","",Date(),Date()) {}

    constructor(proName:String) :
            this(UUID.randomUUID().toString(),proName,"","","",Date(),Date()) {}
}