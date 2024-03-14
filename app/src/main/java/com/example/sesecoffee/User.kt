package com.example.sesecoffee

import java.util.Date
import java.util.UUID

class User(
    val id:String = UUID.randomUUID().toString(),
    var fullName:String,
    var email:String,
    var password:String,
    var address: String,
    val createdAt: Date,
    var updatedAt: Date,
    ) {
    constructor() :
            this(UUID.randomUUID().toString(),"","","","", Date(), Date()) {}

    constructor(fullName:String) :
            this(UUID.randomUUID().toString(),fullName,"","","", Date(), Date()) {}

    constructor(fullName:String,email:String,password:String,address:String) :
            this(UUID.randomUUID().toString(),fullName,email,password,address, Date(), Date()) {}

}